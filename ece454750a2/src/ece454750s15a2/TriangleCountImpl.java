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
	
	public class EnumerateTrianglesParrallel implements Runnable{
		private int startingVertex;
		private ArrayList<LinkedHashSet<Integer>> adjacencyList;
		private ConcurrentMap<String,Triangle> syncMap;
		
		public EnumerateTrianglesParrallel(int startingVertex, ArrayList<LinkedHashSet<Integer>> adjacencyList, ConcurrentHashMap<String,Triangle> syncMap){
			this.startingVertex = startingVertex;
			this.adjacencyList = adjacencyList;
			this.syncMap = syncMap;
		}
		
		public void run(){
			int numEdges = 0;
			int numEdges_A = 0;
			int numEdges_B = 0;
			int vertex_A = 0;
			int vertex_B = 0;
			int numVertices = adjacencyList.size();
			
			for (int vertex_index = startingVertex; vertex_index < numVertices; vertex_index += numCores) {
				LinkedHashSet<Integer> vertex = new LinkedHashSet(adjacencyList.get(vertex_index));
				numEdges = vertex.size();
				if (numEdges > 1){
					Iterator<Integer> iteratorA = vertex.iterator();
					while(iteratorA.hasNext()){
						vertex_A = iteratorA.next();
						LinkedHashSet<Integer> vertexA = adjacencyList.get(vertex_A);
						Iterator<Integer> iteratorB = vertex.iterator();
						iteratorB.next();
						while (iteratorB.hasNext()){
							vertex_B = iteratorB.next();
							if (vertex_A > vertex_B && adjacencyList.get(vertex_B).size() > 0  && adjacencyList.get(vertex_B).contains(vertex_A)){									
								syncMap.putIfAbsent(getTriangleString(vertex_index, vertex_A, vertex_B),new Triangle(vertex_index, vertex_B, vertex_A)); 
							}else if (vertex_B > vertex_A && vertexA.size() > 0  && vertexA.contains(vertex_B)){
								syncMap.putIfAbsent(getTriangleString(vertex_index, vertex_A, vertex_B),new Triangle(vertex_index, vertex_A, vertex_B)); 
							}
						}
							 iteratorA.remove();
					}
				}
			}
		}
	}
	
    public List<Triangle> enumerateTriangles() throws IOException {
	ArrayList<LinkedHashSet<Integer>> adjacencyList = getAdjacencyList(input);
	ArrayList<Triangle> ret = new ArrayList<Triangle>();

	if (numCores == 1){
		int numEdges = 0;
		int vertex_A = 0;
		int vertex_B = 0;
		int numVertices = adjacencyList.size();
		
		for (int vertex_index = 0; vertex_index < numVertices; vertex_index++) {
			LinkedHashSet<Integer> vertex = adjacencyList.get(vertex_index);
			numEdges = vertex.size();
			if (numEdges > 1){
				Iterator<Integer> iteratorA = vertex.iterator();
				while(iteratorA.hasNext()){
					vertex_A = iteratorA.next();
					LinkedHashSet<Integer> vertexA = adjacencyList.get(vertex_A);
					Iterator<Integer> iteratorB = vertex.iterator();
					iteratorB.next();
					while (iteratorB.hasNext()){
						vertex_B = iteratorB.next();
						if (vertex_A > vertex_B && adjacencyList.get(vertex_B).size() > 0  && adjacencyList.get(vertex_B).contains(vertex_A)){
							ret.add(new Triangle(vertex_index, vertex_B, vertex_A));
						}else if (vertex_B > vertex_A && vertexA.size() > 0  && vertexA.contains(vertex_B)){
							ret.add(new Triangle(vertex_index, vertex_A, vertex_B));
						}
					}
					 iteratorA.remove();
				}
			}
			vertex.clear();
		}
	}else{
		ConcurrentHashMap<String,Triangle> syncMap = new ConcurrentHashMap<String, Triangle>();
		
		Thread threads[] = new Thread[numCores];
		for (int i = 0; i < numCores; i++){
			EnumerateTrianglesParrallel thread = new EnumerateTrianglesParrallel(i,adjacencyList,syncMap);
			threads[i] = new Thread(thread);
			threads[i].start();
		}
		
		for (int i = 0; i < numCores; i++){
			try{
				threads[i].join();
			}catch(Exception e){}
		}
		
		List<Triangle> temp = new ArrayList<Triangle>(syncMap.values());
		ret.addAll(temp);
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
	
	public String getTriangleString(int v1,int v2,int v3){	
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
		return x+"_"+y+"_"+"_"+z;
	}
	
	
    public ArrayList<LinkedHashSet<Integer>> getAdjacencyList(byte[] data) throws IOException {
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
 
	ArrayList<LinkedHashSet<Integer>> adjacencyList = new ArrayList<LinkedHashSet<Integer>>(numVertices);
	for (int i = 0; i < numVertices; i++) {
	    adjacencyList.add(new LinkedHashSet<Integer>());
	}
	while ((strLine = br.readLine()) != null && !strLine.equals(""))   {
	    parts = strLine.split(": ");
	    int vertex = Integer.parseInt(parts[0]);
	    if (parts.length > 1) {
		parts = parts[1].split(" +");
		int edge_vertex = 0;
		for (String part: parts) {
			edge_vertex = Integer.parseInt(part);
			if (edge_vertex > vertex){
				adjacencyList.get(vertex).add(edge_vertex);
			}
		}
	    }
	}
	br.close();
	return adjacencyList;
    }
}
