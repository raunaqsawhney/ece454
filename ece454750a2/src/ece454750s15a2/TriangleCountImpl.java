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

	//ConcurrentHashMap<Integer, ArrayList<Integer>> vertex = new ConcurrentHashMap<Integer, ArrayList<Integer>>();
	ArrayList<HashSet<Integer>> adjacencyList = getAdjacencyList(input);
	ArrayList<Triangle> ret = new ArrayList<Triangle>();
	
	/* Modified Sample Code */
	int numEdges = 0;
	int numEdges_A = 0;
	int numEdges_B = 0;
	int vertex_A = 0;
	int vertex_B = 0;
	int numVertices = adjacencyList.size();
	
	for (int vertex_index = 0; vertex_index < numVertices; vertex_index++) {
		HashSet<Integer> vertex = adjacencyList.get(vertex_index);
		numEdges = vertex.size();
		if (numEdges > 1){
			Iterator<Integer> iteratorA = vertex.iterator();
			int loop_number = 0;
			while(iteratorA.hasNext()){
				loop_number++;
				vertex_A = iteratorA.next();
				numEdges_A = adjacencyList.get(vertex_A).size();
				if (numEdges_A > 1){
					Iterator<Integer> iteratorB = vertex.iterator();
					for (int i = 0; i < loop_number; i++){iteratorB.next();}
					while (iteratorB.hasNext()){
						vertex_B = iteratorB.next();
						numEdges_B = adjacencyList.get(vertex_B).size();
						if (numEdges_B > 1 && adjacencyList.get(vertex_A).contains(vertex_B) && adjacencyList.get(vertex_B).contains(vertex_A)){
							ret.add(getTriangle(vertex_index, vertex_A, vertex_B));
						}
					}
				}
				// adjacencyList.get(vertex_A).remove(vertex_index);
			}
			
			Iterator<Integer> removalIterator = vertex.iterator();
			while(removalIterator.hasNext()){
				int temp = removalIterator.next();
				adjacencyList.get(temp).remove(vertex_index);
			}
		}else if(numEdges == 1){
			Iterator<Integer> deleteIterator = vertex.iterator();
			int edgeToDelete = deleteIterator.next();
			int temp_index = edgeToDelete;
			adjacencyList.get(temp_index).remove(vertex_index);
			while (adjacencyList.get(edgeToDelete).size() == 1){
				deleteIterator = adjacencyList.get(edgeToDelete).iterator();
				temp_index = deleteIterator.next();
				adjacencyList.get(temp_index).remove(edgeToDelete);
				edgeToDelete = temp_index;
			}
		}
		vertex.clear();
	}
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
	
	
    public ArrayList<HashSet<Integer>> getAdjacencyList(byte[] data) throws IOException {
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
 
	ArrayList<HashSet<Integer>> adjacencyList = new ArrayList<HashSet<Integer>>(numVertices);
	for (int i = 0; i < numVertices; i++) {
	    adjacencyList.add(new HashSet<Integer>());
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
