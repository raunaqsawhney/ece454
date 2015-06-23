/**
 * ECE 454/750: Distributed Computing
 *
 * Code written by Wojciech Golab, University of Waterloo, 2015
 *
 * IMPLEMENT YOUR SOLUTION IN THIS FILE
 *
 */

package ece454750s15a2;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class TriangleCountImpl {
    private byte[] input;
    private int numCores;

    public TriangleCountImpl(byte[] input, int numCores) {
	this.input = input;
	this.numCores = numCores;
    }

    public List<String> getGroupMembers() {
	ArrayList<String> ret = new ArrayList<String>();
	ret.add("rsawhney");
	ret.add("t39chan");
	return ret;
    }

    public List<Triangle> enumerateTriangles() throws IOException {

	ConcurrentHashMap<Integer, ArrayList<Integer>> vertex = new ConcurrentHashMap<Integer, ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> adjacencyList = getAdjacencyList(input);
	ArrayList<Triangle> ret = new ArrayList<Triangle>();

	int numVertices = adjacencyList.size();
	for (int i = 0; i < numVertices; i ++){
		if (adjacencyList.get(i).size() > 1){
			vertex.put(i,adjacencyList.get(i));
		}
	}

	Iterator entryList = vertex.entrySet().iterator();
	while (entryList.hasNext()){
		Object temp_object = entryList.next();
		Map.Entry<Integer, ArrayList<Integer>> temp_map_entry = (Map.Entry<Integer, ArrayList<Integer>>) temp_object;
		Iterator vertexList = temp_map_entry.getValue().iterator();
		while(vertexList.hasNext()){
			Object temp_vertex = vertexList.next();
			if (!vertex.containsKey(temp_vertex) || vertex.get(temp_vertex) == null){
				vertexList.remove(); 
			}
		}
		
		if (temp_map_entry.getValue().size() > 1){
			vertex.put(temp_map_entry.getKey(),temp_map_entry.getValue());
		}else{
			if (temp_map_entry.getValue().size() == 1){
				int last_edge = temp_map_entry.getValue().get(0);
				vertex.get(last_edge).remove(temp_map_entry.getKey());
				if (vertex.get(last_edge).size() == 0){
					vertex.remove(last_edge);
				}else if (vertex.get(last_edge).size() < 2 && vertex.get(vertex.get(last_edge).get(0)) != null){
					int temp_index = vertex.get(vertex.get(last_edge).get(0)).indexOf(last_edge);
					vertex.get(vertex.get(last_edge).get(0)).remove(temp_index);
					vertex.remove(last_edge);
				} 
			}
			
			entryList.remove();
		}
	}
	
	/*
	for (Map.Entry<Integer, ArrayList<Integer>> x : vertex.entrySet()){
		System.out.print(x.getKey() + ":");
		for (int i : x.getValue()){ 
			System.out.print(i + " ");
		}
		System.out.println();
	}*/
	
	for (Map.Entry<Integer, ArrayList<Integer>> x : vertex.entrySet()){
		int num_of_edges = x.getValue().size();
		int adj_vertex_1 = -1;
		int adj_vertex_2 = -1;
		
		int vertex_num = x.getKey();
		
		for (int index_1 = 0; index_1 < num_of_edges; index_1++){
			adj_vertex_1 = x.getValue().get(index_1);
			for (int index_2 = index_1 + 1; index_2 < num_of_edges; index_2++){
				adj_vertex_2 = x.getValue().get(index_2);
				if (vertex.get(adj_vertex_1).contains(adj_vertex_2) && vertex.get(adj_vertex_2).contains(adj_vertex_1)){
					ret.add(getTriangle(vertex_num, adj_vertex_1, adj_vertex_2));
				}
			}
			
			for (int i = 0; i < num_of_edges; i++){
				// TODO Remove edges from other vertex lists to stop duplicates
			}
		}
	}

	// For each vertex in map
		// Use two indexes iterating through adjacencyList of the vertex
		// Check to see that the adjacency lists of the vertexes pointed by indexes contains the other
		// If it does then a triangle is formed
		
	// Once the vertex is finished
		// Remove the current vertex from the adjacent lists of each vertex in its lists
		// Remove vertex from map
	
	
	/* Modified Sample Code
	for (int i = 0; i < numVertices; i++) {
		ArrayList<Integer> n1 = adjacencyList.get(i);
		//if (n1.size() > 1){
			//System.out.println(i); 
			for (int j: n1) {
				ArrayList<Integer> n2 = adjacencyList.get(j);
				//if (j != i && n2.size() > 1){
					for (int k: n2) {
						ArrayList<Integer> n3 = adjacencyList.get(k);
						//if (k != i && k != j && n3.size() > 1){
							for (int l: n3) {
								if (i < j && j < k && l == i) {
									ret.add(new Triangle(i, j, k));
								}
							}
						//}
					}
				//}
			}
		//}
	}*/

	return ret;
    }

	public Triangle getTriangle(int v1,int v2,int v3){	
		int x = -1,y = -1,z = -1;
		if (v1 < v2 && v1 < v3){
			x = v1;
			if (v2 < v3){y = v2;z = v3;}else{y = v3;z = v2;}
		}else if(v2 < v1 && v2 < v3){
			x = v2;
			if (v1 < v3){y = v1;z = v3;}else{y = v3;z = v1;}
		}else if(v3 < v1 && v3 < v2){
			x = v3;
			if (v1 < v2){y = v1;z = v2;}else{y = v2;z = v1;}
		}
		return new Triangle(x,y,z);
	}
	
	
    public ArrayList<ArrayList<Integer>> getAdjacencyList(byte[] data) throws IOException {
	InputStream istream = new ByteArrayInputStream(data);
	BufferedReader br = new BufferedReader(new InputStreamReader(istream));
	String strLine = br.readLine();
	if (!strLine.contains("vertices") || !strLine.contains("edges")) {
	    System.err.println("Invalid graph file format. Offending line: " + strLine);
	    System.exit(-1);	    
	}
	String parts[] = strLine.split(", ");
	int numVertices = Integer.parseInt(parts[0].split(" ")[0]);
	int numEdges = Integer.parseInt(parts[1].split(" ")[0]);
	System.out.println("Found graph with " + numVertices + " vertices and " + numEdges + " edges");
 
	ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<ArrayList<Integer>>(numVertices);
	for (int i = 0; i < numVertices; i++) {
	    adjacencyList.add(new ArrayList<Integer>());
	}
	while ((strLine = br.readLine()) != null && !strLine.equals(""))   {
	    parts = strLine.split(": ");
	    int vertex = Integer.parseInt(parts[0]);
	    if (parts.length > 1) {
		parts = parts[1].split(" +");
		for (String part: parts) {
		    adjacencyList.get(vertex).add(Integer.parseInt(part));
		}
	    }
	}
	br.close();
	return adjacencyList;
    }
}
