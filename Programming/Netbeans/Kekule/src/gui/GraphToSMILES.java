package gui;

import graphs.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import shared.BitVector;
import shared.Cell;
import shared.Utils;

/**
 * This class is used to convert a graph into the Simplified Molecular
 * Input Line Entry System or SMILES. The process is as follows:
 * 
 * Find spanning tree of graph. Take original graph and subtract spanning tree. All 
 * edges in this graph complete cycles, so for each edge, we must label both nodes of the 
 * edge with a following number to remember they are connected. 
 * 
 * Now we can DFS through the spanning tree and just write out the nodes and we reach them.
 * 
 * @author Aaron
 *
 */
public class GraphToSMILES {
	
	private static String SMILES = "";
	private static HashMap<BitVector, List<Integer>>nodeNumbers;
	/**
	 * Converts a graph g, into a String of SMILES format.
	 * @param g
	 * @return
	 */
	public static String convertSMILES(Graph g){
		SMILES = "";
		
		//find spanning tree by DFS-ing the nodes
		Graph spanningTree = getSpanningTree( g );
		
		//subtract original by spanning tree
		//to get edges that make cycles
		Cell originalEdges = g.getEdgeCell();
		Cell treeEdges = spanningTree.getEdgeCell();
		ArrayList<BitVector> extraEdges = new ArrayList<BitVector>();
		for(int i = 0; i < originalEdges.size(); i++){
			BitVector edge = originalEdges.getPA()[i];
			//if edge not in spanning tree
			//then it completes cycle add to extra edges
			if( !treeEdges.contains(edge) ){
				extraEdges.add(edge);
			}
		}
		
		
		//for each extra edge, remember those nodes with number
		//use hash map to store those numbers
		nodeNumbers = new HashMap<BitVector, List<Integer>>();
		int number = 1;
		
		//place the nodes in the hash map
		for(int i = 0; i < extraEdges.size(); i++){
			BitVector edge = extraEdges.get(i);
			//get both nodes of the edge
			BitVector node1 = new BitVector( edge.firstBit() );
			BitVector node2 = new BitVector( edge.getNumber() - node1.getNumber() );
			
			putMap(node1, number);
			putMap(node2, number);
			
			number++;
		}
		
		//DFS the spanning tree and output String
		ArrayList<BitVector> reached = new ArrayList<BitVector>();
		DFS(spanningTree, new BitVector(1), reached);
		return SMILES;
	}

	/**
	 * DFS A graph and outputs the SMILES representation of it. Graph must be a
	 * spanning tree and hash map above must be filled out properly with any 
	 * node cycles
	 * TODO slow and bad
	 */
	public static void DFS(Graph g, BitVector currentNode, ArrayList<BitVector> reached){
		
		reached.add(currentNode);
		
		boolean flag = false;
		if(SMILES.endsWith("(")){
			flag = true;
		}
		SMILES += getName( g, currentNode );
		
		ArrayList<BitVector> neighbours = g.getAllNeighbours(currentNode);
		neighbours.removeAll(reached);
		
		for(BitVector neighbour: neighbours){
			if(neighbour.equals( neighbours.get( neighbours.size() - 1)) ){
				DFS(g, neighbour, reached);
			} else{
				SMILES += "(";
				DFS(g, neighbour, reached);
			}
		}
		if(flag){
			SMILES += ")";
		}
	}
	
	/**
	 * Returns the name of this bitvector. 
	 * Name is C, plus a number from the hash map table maybe
	 * @param b
	 * @return
	 */
	public static String getName( Graph g, BitVector b){
		String name = "";
		if( g.isPort( b ) ){
			name = "P";
		} else{
			name = "c";
		}
		if( nodeNumbers.containsKey(b)){
			ArrayList<Integer> l = (ArrayList<Integer>) nodeNumbers.get(b);
			for(int i: l){
				name += (i + "");
			}
		}
		return name;
	}
	/**
	 * Adds a value to the hash map. When two values hash to the same position, 
	 * they are added to the same list, rather than replace each other. This is
	 * because one node may be part of multiple cycles
	 * @param b, bitvector
	 * @param number, numbesr
	 */
	public static void putMap(BitVector b, int number){
		if( nodeNumbers.containsKey(b) ){
			nodeNumbers.get(b).add(number);
		} else{
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(number);
			nodeNumbers.put(b, list);
		}
	}
	
	/**
	 * Returns a spanning tree of this graph by BFS from node 0 and adding 
	 * edges to nodes it hasn't reached before.
	 */
	public static Graph getSpanningTree(Graph g){
		
		// holds the nodes we have already reached
		HashMap<BitVector, Boolean> reached = new HashMap<BitVector, Boolean>();
		int numberReached = 0;

		//start at node 0
		BitVector node0 = new BitVector(1);
		
		//edges in spanning tree
		ArrayList<BitVector> treeEdges = new ArrayList<BitVector>();
		//edges in normal graph
		BitVector[] edges = g.getEdgeCell().getPA();
		
		//start searching at node 1
		BitVector first = new BitVector(1);
		LinkedList<BitVector> openList = new LinkedList<BitVector>();
		openList.add(first);
		
		while( !openList.isEmpty() ){
			
			BitVector parent = openList.pop();
			reached.put( parent, true );
			numberReached++;
			
			//search all edges on current node
			for(BitVector edge : edges){
				//if edges goes from this node to another
				if(edge.contains( parent.getNumber() ) ){
					//find out who another is
					BitVector another = new BitVector( edge.getNumber() - parent.getNumber() );
					
					//if we haven't already been to another
					if( reached.get( another ) == null && !openList.contains(another) ){
						//add to openList
						openList.add(another);
						treeEdges.add( edge );
					}
				}
			}
		}
		
		Cell c = new Cell( Utils.listToArBV(treeEdges));
		c.setNumPorts( g.getNumPorts() );
		Graph tree = new Graph(g.getName() + "Tree", g.getNumPorts(), g.getNumNodes(), c);
		
		return tree;
	}
	
	/**
	 * DFS a graph to obtain the spanning tree
	 * Only add edges if it reaches a node we haven't seen before
	 * @param edges
	 * @param currentNode
	 */
	//public void DFS(ArrayList<BitVector> edges, BitVector currentNode){
	//	
	//	
	//}
}
