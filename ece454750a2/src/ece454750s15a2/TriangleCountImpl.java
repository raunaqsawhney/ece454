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
	// this code is single-threaded and ignores numCores

	HashMap<Integer, ArrayList<Integer>> vertex = new HashMap<Integer, ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> adjacencyList = getAdjacencyList(input);
	ArrayList<Triangle> ret = new ArrayList<Triangle>();

	int numVertices = adjacencyList.size();
	for (int i = 0; i < numVertices; i ++){
		if (adjacencyList.get(i).size() > 1){
			vertex.put(i,adjacencyList.get(i));
		}
	}
	
	//for (Map.Entry<Integer, ArrayList<Integer>> x : vertex.entrySet()){
	Iterator entryList = vertex.entrySet().iterator();
	while (entryList.hasNext()){
		Object temp_object = entryList.next();
		Map.Entry<Integer, ArrayList<Integer>> temp_map_entry = (Map.Entry<Integer, ArrayList<Integer>>) temp_object;
		Iterator vertexList = temp_map_entry.getValue().iterator();
		while(vertexList.hasNext()){
			Object temp_vertex = vertexList.next();
			if (!vertex.containsKey(temp_vertex)){
				vertexList.remove(); 
			}
		}
		
		if (temp_map_entry.getValue().size() > 1){
			vertex.put(temp_map_entry.getKey(),temp_map_entry.getValue());
		}else{
			entryList.remove();
		}
	}
	
	for (Map.Entry<Integer, ArrayList<Integer>> x : vertex.entrySet()){
		System.out.print(x.getKey() + ":");
		for (int i : x.getValue()){ 
			System.out.print(i + " ");
		}
		System.out.println();
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
