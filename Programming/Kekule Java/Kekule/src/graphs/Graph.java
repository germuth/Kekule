package graphs;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import shared.BitVector;
import shared.Cell;

import makeCell.GraphtoCell;


/**
 * Graph
 * 
 * This represents an undirected, simple, (connected? not yet) graph.
 * 
 * In Kekule theory, certain nodes of the graphs are called ports and represent connections outside the graph. 
 * The graph keeps track of the number of total nodes, the number of ports, and which nodse are ports. 
 * The Edges of this graph are stored in an adjacency matrix, although each Edge is converted to BitVector
 * later in the program.
 * 
 * The Number of Nodes must never exceed 32 since the set of nodes is represented in BitVectors, and the BitVector
 * for a set of 32 size would overflow the integer.
 * cP <= cN <= 32
 * 
 * TODO this classed could be re-worked to conform with the rest of the program better
 * 
 * @author Aaron
 *
 */
public class Graph {
	
	/**
	 * assume g1 numPorts >= g2 numPorts 
	 * destroys g1 and replaces with odot
	 */
	public static Graph oDot(Graph g1, Graph g2){
		Set<BitVector> g1Edges = new HashSet<BitVector>();
		for(int i = 0; i < g2.getEdgeCell().size(); i++){
			g1Edges.add( renameEdge( g2.edgeCell.getPA()[i], g2.numPorts,
					g1.numNodes - g2.numPorts ) );
		}
		g1.setEdgeCell(new Cell(g1Edges, 0));
		g1.setNumNodes( g1.numNodes + g2.numNodes - g2.numPorts );
		return g1;
	}
	
	private static BitVector renameEdge(BitVector edge, int from, int over){
		int k = 1;
		int i = 0;
		while( edge.contains(k) ){
			k *= 2;
			i++;
		}
		int answer;
		if( i < from){
			answer = k;
		} else{
			answer = k << over;
		}
		
		do{
			k *= 2;
			i++;
		} while( edge.contains(k) );
		
		if( i < from){
			answer += k;
		} else{
			answer += k << over;
		}
		
		return new BitVector(answer);
		
	}
	
	/**
	 * Name of the graph
	 */
	private String name;
	/**
	 * The number of ports in this graph
	 */
	private int numPorts;
	/**
	 * The number of nodes in this graph.
	 * Includes the ports
	 */
	private int numNodes;
	
	private Cell edgeCell;

	// take in graph from input
	public Graph(String name, int nP, int nC, Set<String> edges) {
		Set<BitVector> bvEdges = new HashSet<BitVector>();

		for (String x : edges) {
			Scanner edgeScanner = new Scanner(x);
			int firstNode = edgeScanner.nextInt();
			int secondNode = edgeScanner.nextInt();
			
			//turn each node number into bit vector (<<)
			//the add together to get bit vector of two nodes
			//each node in the edge
			bvEdges.add(new BitVector(
					(1 << firstNode) + 
					(1 << secondNode) ) );
			edgeScanner.close();
		}
		
		this.name = name;
		this.numNodes = nC;
		this.numPorts = nP;
		this.edgeCell = new Cell(bvEdges, numPorts);
	}
	
	public Graph(int nP, int nC, Cell edges){
		this.numPorts = nP;
		this.numNodes = nC;
		this.edgeCell = edges;
		this.name = "";
	}
	
	public Graph(Graph g){
		this.name = g.name;
		this.numNodes = g.numNodes;
		this.numPorts = g.numPorts;
		this.edgeCell = new Cell(g.edgeCell);
	}
	
	
	
	@Override
	public boolean equals(Object obj) {
		Graph another = (Graph) obj;
		this.edgeCell.sortBySize();
		another.edgeCell.sortBySize();
		if(this.edgeCell.equalsNoPorts(another.edgeCell)){
			return true;
		}
		return false;
	}

	public void addEdge(BitVector bv){
		this.edgeCell.add(bv);
	}
	
	public int getHighestDegree(){
		int max = -1;
		int lastNode = 1 << ( this.numNodes - 1 );
		//cycle through all nodes
		for(int node = 1; node <= lastNode; node *= 2 ){
			
			int degree = 0;
			Cell edges = this.getEdgeCell();
			//cycle through edges and count occurences of that node
			for(int i = 0; i < edges.size(); i++){
				BitVector edge = edges.getPA()[i];
				if(edge.contains(node)){
					degree++;
				}
			}
			
			if(degree > max){
				max = degree;
			}
		}
		
		return max;
	}
	
	public int getHighestPortDegree(){
		int max = -1;
		int lastNode = 1 << ( this.numPorts - 1 );
		//cycle through all nodes
		for(int node = 1; node <= lastNode; node *= 2 ){
			
			int degree = 0;
			Cell edges = this.getEdgeCell();
			//cycle through edges and count occurences of that node
			for(int i = 0; i < edges.size(); i++){
				BitVector edge = edges.getPA()[i];
				if(edge.contains(node)){
					degree++;
				}
			}
			
			if(degree > max){
				max = degree;
			}
		}
		
		return max;
	}
	
	/**
	 * Translates a graph over a Bit Vector
	 * @param bv
	 */
	public void translate(BitVector bv){
		Cell edges = this.edgeCell;

		while( !bv.isEmpty() ){
			int k = bv.firstNode();
			bv = new BitVector(bv.getNumber() - k);
			
			//new node we are adding
			BitVector p = new BitVector(1 << this.numNodes);
			
			//if any existing edges touch translation,
			//add edge from new node to what current and bv don't share
			for(int i = 0; i < edges.size(); i++){
				BitVector current = edges.getPA()[i];
				if(current.contains(k)){
					int x = current.getNumber() + p.getNumber() - k;
					edges.getPA()[i] = new BitVector(x);
				}
			}
			edges.add(new BitVector( p.getNumber() + k ));
			this.numNodes++;
		}
		
		edges.sortBySize();
	}
	
	//TODO not
	//TODO even
	//TODO close
	//TODO to
	//TODO finished
	public void minimizeGraph(){
		Cell edges = this.edgeCell;
		if(edges.size() == 0){
			return;
		}
		int edgesSize = edges.size();
		Cell cell =  GraphtoCell.makeCell(this);
		BitVector lastRemoved = null;
		
		edgesSize--;
		
		int i = 0;
		while( i <= edgesSize){
			BitVector edge = edges.getPA()[i];
			edges.getPA()[i] = edges.getPA()[edgesSize];
			
			Cell aCell = GraphtoCell.makeCell(this);
			
			//TODO minimization of graphs
			if( false){
			//if( aCell.isSubSetOf(cell) ){
			//if( cell.isSubSetOf(aCell)){
				System.out.println("Removing Edge");
				cell = aCell;
				edgesSize--;;
			} else{
				edges.getPA()[i] = edge;
				i++;
			}
		}
		edgesSize++;
	}
	
	public void writeGraph(){
		String title = "";
		if(this.name != null){
			title += this.name + ": ";
		}
		title += this.numNodes +" Nodes, " + " " + this.numPorts + " Ports";
		System.out.println(title);
		String edges = "Edges: ";
		for(int i = 0; i < this.edgeCell.size(); i++){
			BitVector edge = this.edgeCell.getPA()[i];
			int p = edge.firstBit();
			edge = new BitVector( edge.getNumber() - ( 1 << p ) );
			int q = edge.firstBit();
			edges += ( p ) + "-" + ( q );
			
			if(i != this.edgeCell.size() - 1){
				edges += ", ";
			}
		}
		System.out.println(edges);
		System.out.println("");
	}
	
	public void writeEdges(){
		String edges = "Edges: ";
		for(int i = 0; i < this.edgeCell.size(); i++){
			BitVector edge = this.edgeCell.getPA()[i];
			int p = edge.firstBit();
			edge = new BitVector( edge.getNumber() - ( 1 << p ) );
			int q = edge.firstBit();
			edges += ( p ) + "-" + ( q );
			
			if(i != this.edgeCell.size() - 1){
				edges += ", ";
			}
		}
		System.out.println(edges);
		System.out.println("");
	}
	
	public void writeTitle(){
		String title = "";
		if(this.name != null){
			title += this.name + ": ";
		}
		title += this.numNodes +" Nodes, " + " " + this.numPorts + " Ports";
		System.out.println(title);
	}
	
	/**
	 * Returns the set of nodes represented in a BitVector
	 * @return, bitvector of all nodes
	 */
	public BitVector getNodeVector(){
		//bitVector = 1 * 2 ^ nodes   - 1
		//gives you x 1s, where node = x
		int bitVector = ( 1 << numNodes ) - 1;
		return new BitVector(bitVector);
	}
	
	/**
	 * Returns the set of ports in a bitvector. At this point, ports
	 * should be nodes 0 - ports - 1
	 * @return
	 */
	public BitVector getPortVector(){
		int bitVector = ( 1 << numPorts ) - 1;
		return new BitVector(bitVector);
	}

	public String getName() {
		return name;
	}
	
	public void addTwoNodes(){
		this.numNodes += 2;
	}

	public Cell getEdgeCell() {
		return edgeCell;
	}

	public void setEdgeCell(Cell edgeCell) {
		this.edgeCell = edgeCell;
	}

	public int getNumPorts() {
		return numPorts;
	}
	
	public int getNumNodes() {
		return numNodes;
	}

	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}
	
	public void setName(String name2) {
		this.name = name2;
		
	}
}
