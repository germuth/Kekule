package bitvectors;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

/**
 * Reads in a graph and outputs it's Kekule cell
 * @author Aaron
 *
 */
public class GraphtoCell {

	private static File f;
	private static Scanner s;
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		f = new File("graphs.txt");
		s = new Scanner(f);
		Graph inputGraph = null;
		
		try{
			inputGraph = readGraph();			
		} catch(NoSuchElementException e){
			System.out.println("file invalid");
			System.exit(0);
		}
		
		while(inputGraph != null){
			Set<BitVector> kCell = makeCell(inputGraph.getNodeVector(), inputGraph);
			Cell kekule = new Cell(kCell, inputGraph.getNumPorts());
			
			kekule.sortBySize();
			
			System.out.println(kekule.printUnweighted());
			
			kekule.normalize();
		
			try{
				inputGraph = readGraph();
			} catch(NoSuchElementException e){
				inputGraph = null;
			}
		}
		
	}
	
	private static Set<BitVector> makeCell(BitVector bvNodes, Graph g){
		
		Set<BitVector> kekuleCell = new HashSet<BitVector>();
		Set<BitVector> addend = new HashSet<BitVector>();
		
		BitVector ports = g.getPortVector();
		Set<BitVector> edges = g.getEdges();
		
		//Base case
		if(bvNodes.isEmpty()){
			kekuleCell.add(new BitVector(0));
		}
		else{
			//grab first Vertex from nodes
			//does not remove it
			int nodeU = bvNodes.firstNode();
			//remove vertex from nodes
			bvNodes = bvNodes.remove(nodeU);
			
			if(ports.contains(nodeU)){
				//place new bit vector to ensure different object
				kekuleCell = makeCell(new BitVector(bvNodes), g);
			}
			else{
				kekuleCell.clear();
			}
			
			
			//treat ndh(u,g)
			//iterate over all edges
			Iterator<BitVector> i = edges.iterator();
			while(i.hasNext()){
				BitVector edge = (BitVector) i.next(); 
				//if u in edge
				if(edge.contains(nodeU)){
					
					//remove node U from edge
					BitVector newEdge = new BitVector(edge);
					newEdge = newEdge.remove(nodeU);
					//grab other node from edge
					int nodeV = newEdge.firstNode();
							
					///if nodes has nodeV
					if(bvNodes.contains(nodeV)){
						
						//remove v from nodes
						BitVector newNodes = new BitVector(bvNodes);
						newNodes = newNodes.remove(nodeV);
						
						//find sub cell
						addend = makeCell(newNodes, g);
						
						//create bit vector we will use as translation
						BitVector portAssignment = BitVector.intersection(
								ports,
								new BitVector(nodeU + nodeV) );
						//translate over port assignment bit vector
						addend = Utils.translate(addend, portAssignment);
						
						//take union of answer and addend
						
						kekuleCell = Utils.union(
								kekuleCell,
								addend );
										
					}
				}
			}
		}
		
		return kekuleCell;
	}
	
	/**
	 * Reads a single graph from graphs.txt and returns it as Graph object
	 * @return
	 * @throws FileNotFoundException
	 */
	private static Graph readGraph() throws NoSuchElementException{
		
		String name = s.nextLine();
		int numNodes = s.nextInt();
		int numPorts = s.nextInt();
		
		s.nextLine();
		
		String ports = s.nextLine();
		
		int[] portRemapping = getPortPermutation(numNodes, numPorts, ports);
		
		String inputEdges = s.nextLine();
		String inputExtraEdges = s.nextLine();
		
		Set<String> edges = parseForEdgesCompact(inputEdges, portRemapping);
		
		if(!inputExtraEdges.isEmpty()){
			Set<String> extraEdges = parseForEdges(inputExtraEdges, portRemapping);
			edges.addAll(extraEdges);
			s.nextLine();
		}
		
		return new Graph(name, numPorts, numNodes, edges);
	}
	
	/**
	 * Remaps the nodes so the ports are the first 0 - numPorts nodes. This allows the edge numbers to be changed
	 * based off of the new node permutation
	 */
	private static int[] getPortPermutation(int nodeNum, int portNum, String ports){
		int[] remapping = new int[nodeNum];
		
		for(int i = 0; i < remapping.length; i++){
			remapping[i] = -1;
		}
		
		Scanner s = new Scanner(ports);
		//keep track of current node
		int currentNode = 0;
		
		while(s.hasNext()){
			String num = s.next();
			int number = Integer.parseInt(num);
			remapping[number] = currentNode++;
		}
		
		for(int i = 0; i < remapping.length; i++){
			if(remapping[i] < 0){
				remapping[i] = currentNode++;
			}
		}
		s.close();
		
		return remapping;
	}

	/**
	 * Parses long string of form
	 * 0-1-2-3-4-5-6-7-8-9 and returns 
	 * string array of format {0 1, 1 2, 2 3, 3 4, 4 5, etc}
	 * @param inputEdges
	 * @return
	 */
	private static Set<String> parseForEdgesCompact(String inputEdges, int[] remapping) {
		Set<String> edges = new HashSet<String>();
		
		//should get 
		//{ "1", "2", "3", "4" } etc
		//numbers are at every even index of array
		String[] edgeArray = inputEdges.split("-");
		
		int index = 0;
		String first = edgeArray[index];
		index++;
		first = first.trim();
		String second = edgeArray[index];
		index++;
		second = second.trim();
		
		//convert first and second to ints
		int fir = Integer.parseInt(first);
		int sec = Integer.parseInt(second);
		
		//use remapping
		edges.add(remapping[fir] + " " + remapping[sec]);
		
		while(index < edgeArray.length){
			first = second;
			second = edgeArray[index];
			second = second.trim();
			index++;
			
			//convert first and second to ints
			fir = Integer.parseInt(first);
			sec = Integer.parseInt(second);
			
			//use remapping
			edges.add(remapping[fir] + " " + remapping[sec]);
		}
		
		return edges;
	}
	
	/**
	 * Parses long string of form
	 * 0-1, 5-6, 8-9 and returns 
	 * string array of format {0 1, 1 2, 2 3, 3 4, 4 5, etc}
	 * @param inputEdges
	 * @return
	 */
	private static Set<String> parseForEdges(String inputEdges, int[] remapping) {
		Set<String> extraEdges = new HashSet<String>();
		
		//should get 
		//{ "0-1", "5-6", "8-9"} etc
		//ranges are at every even index of array
		String[] edgeArray = inputEdges.split(",");
		
		int index = 0;
		while(index < edgeArray.length){
			//grab every second element
			String edge = edgeArray[index];
			index++;
			//use other method to parse 0-1, and return "0 1"
			Set<String> singleEdge = parseForEdgesCompact(edge, remapping);
			//add to set of extra edges
			extraEdges.addAll(singleEdge);
		}
		
		return extraEdges;
	}

}
