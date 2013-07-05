package graphs;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

import javax.media.j3d.TriangleStripArray;

import shared.BitVector;
import shared.Cell;
import shared.GraphtoCell;
import shared.PowerSet;


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
public class Graph implements Comparable<Graph>{
	
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
	/**
	 * Cell containing all the edges of this graph in 
	 * bitvector form
	 */
	private Cell edgeCell;

	/*
	 * rank given to this graph based on the Genetic algorithms fitness
	 * function
	 */
	private int rank;
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
	
	public Graph(String name, int nP, int nC, Cell edges){
		this.numPorts = nP;
		this.numNodes = nC;
		this.edgeCell = edges;
		this.name = name;
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
	
	public void removeEdge(BitVector bv){
		//if doesn't contain edge
		if( !this.edgeCell.contains(bv) ){
			return;
		}
		int ports = this.edgeCell.getNumPorts();
		
		BitVector[] edges = new BitVector[ this.edgeCell.size() - 1 ];
		int k = 0; 
		
		for(int i = 0; i < this.edgeCell.size(); i++){
			BitVector currentEdge = this.edgeCell.getPA()[i];
			if( !currentEdge.equals(bv) ){
				edges[k] = currentEdge;
				k++;
			}
		}
		this.edgeCell = new Cell( edges );
		this.edgeCell.setNumPorts( ports );
	}
	
	public int getHighestDegree(){
		int max = -1;
		int lastNode = 1 << ( this.numNodes - 1 );
		//cycle through all nodes
		for(int node = 1; node <= lastNode; node *= 2 ){
			
			int degree = 0;
			Cell edges = this.getEdgeCell();
			//cycle through edges and count occurrences of that node
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
	
	public int getDegree(BitVector bv){
		
		int node = bv.getNumber();
		
		int degree = 0;
		Cell edges = this.getEdgeCell();
		//cycle through edges and count occurences of that node
		for(int i = 0; i < edges.size(); i++){
			BitVector edge = edges.getPA()[i];
			if(edge.contains(node)){
				degree++;
			}
		}
		return degree;
	}
	
	public Set<BitVector> getAllNodesWithDegree1(){
		Set<BitVector> degree1List = new HashSet<BitVector>();
		
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
			
			if(degree == 1){
				degree1List.add( new BitVector(node) );
			}
		}
		
		return degree1List;
	}
	
	//extends every port of the graph out one node, and replaces it with internal vertex
	//only returns graph if new graph has the same cell as the old one
	//else null
	//every port must have at least degree 1
	public Graph extendPorts(){
		Cell cell = GraphtoCell.makeCell(this);
		cell.normalize();
		
		//extended graph
		Graph extended = new Graph(this);
		
		int lastNode = extended.getLastNode().getNumber();
		//ports always 0 1 2 3 4
		for(int port = 1; port <= (1 << ( extended.numPorts -1 ) ); port *=2){
			
			//add new node to replace port
			int newNode = lastNode*2;
			lastNode = newNode;
			extended.numNodes++;
			
			//find all edges which connect to extended port
			for(int i = 0; i < extended.edgeCell.size(); i++){
				BitVector edge = extended.edgeCell.getPA()[i];
				if( edge.contains(port) ){
					
					int otherNode = edge.getNumber() - port;
					
					//delete edges from port to other
					extended.removeEdge(edge);
					//add edge from port to other
					extended.addEdge( new BitVector( otherNode + newNode ) );
				}
			}
			
			extended.addEdge( new BitVector( newNode + port) );
		}
		
		extended.getEdgeCell().sortBySize();
		Cell newCell = GraphtoCell.makeCell(extended);
		if(newCell.size() != 0){
			newCell.normalize();
		}
		if( !newCell.equalsNoPorts(cell) ){
			//extended = new Graph(this);
			return null;
		}
		extended.setName( extended.getName() + "Extended");
		return extended;
	}
	
	//extends every port of the graph out one node, and replaces it with internal vertex
	//returns graph regardless of cell
		public Graph extendPortsNoCell(){
			//extended graph
			Graph extended = new Graph(this);
			
			int lastNode = extended.getLastNode().getNumber();
			//ports always 0 1 2 3 4
			for(int port = 1; port <= (1 << ( extended.numPorts -1 ) ); port *=2){
				
				//add new node to replace port
				int newNode = lastNode*2;
				lastNode = newNode;
				extended.numNodes++;
				
				//find all edges which connect to extended port
				for(int i = 0; i < extended.edgeCell.size(); i++){
					BitVector edge = extended.edgeCell.getPA()[i];
					if( edge.contains(port) ){
						
						int otherNode = edge.getNumber() - port;
						
						//delete edges from port to other
						extended.removeEdge(edge);
						//add edge from port to other
						extended.addEdge( new BitVector( otherNode + newNode ) );
					}
				}
				
				extended.addEdge( new BitVector( newNode + port) );
			}
			
			extended.getEdgeCell().sortBySize();
			Cell newCell = GraphtoCell.makeCell(extended);
			if(newCell.size() != 0){
				newCell.normalize();
			}
			extended.setName( extended.getName() + "Extended");
			return extended;
		}
	
	//takes this graph and adds some nodes/edges to connect it
	//makes sure it has the same cell
	public Graph connect(Cell cell){
		
		//get all nodes of degree == 1
		Set<BitVector> degreeOne = this.getAllNodesWithDegree1();
		
		//get all pairs of such nodes
		PowerSet<BitVector> pairs = new PowerSet<BitVector>(degreeOne, 2, 2);
		Iterator<Set<BitVector>> i = pairs.iterator();
		
		//and for every pair, try adding connection there, and see if it is still disjoint
		//and whether it still has same cell
		while( i.hasNext() ){
			Set<BitVector> pair = i.next();
			
			if(pair.isEmpty()){
				continue;
			}
			
			Iterator<BitVector> i2 = pair.iterator();
			int first = i2.next().getNumber();
			int second = i2.next().getNumber();
			
			//new graph
			Graph connected = new Graph(this);
			//get last node so we know number of new two nodes we are adding
			int lastNode = 1 << ( this.numNodes - 1 );
			int newNode1 = lastNode*2;
			int newNode2 = newNode1*2;
			//add two new nodes
			connected.addTwoNodes();
			
			//add edge from new1 -> new2
			connected.addEdge(new BitVector( newNode1 + newNode2 ));
			//add edges from port 1 to new1
			connected.addEdge(new BitVector( first + newNode1) );
			//and edge from port 2 to new1
			connected.addEdge(new BitVector( second + newNode1) );
			
			//if no longer disjoint
			//AND has the same cell
			//then we did it!
			if( !connected.isDisjoint() ){
				if(GraphtoCell.makeCell(connected).equalsNoPorts(cell) ){
					return connected;
				}
			}
		}
		
		return null;
	}
	
	public Graph triangleFree(Cell cell) {

		//all triangles
		Set<Set<BitVector>> allTriangles = new HashSet<Set<BitVector>>();
		
		// get all nodes 
		Set<BitVector> nodes = new HashSet<BitVector>();
		int lastNode = 1 << ( this.numNodes - 1 );
		
		//cycle through all nodes
		//if degree is 1, can't be in triangle
		for(int node = 1; node <= lastNode; node *= 2 ){
			BitVector temp = new BitVector( node );
			if(this.getDegree( temp ) > 1){
				nodes.add( new BitVector(node) );				
			}
		}

		// get all triples of nodes
		PowerSet<BitVector> pairs = new PowerSet<BitVector>(nodes, 3, 3);
		Iterator<Set<BitVector>> i = pairs.iterator();

		//for every triplets, check if they are connected to each others
		//if so triangle!
		while (i.hasNext()) {
			Set<BitVector> pair = i.next();
			if (pair.isEmpty()) {
				continue;
			}
			Iterator<BitVector> i2 = pair.iterator();
			
			int node1 = i2.next().getNumber();
			int node2 = i2.next().getNumber();
			int node3 = i2.next().getNumber();
			
			boolean edge12 = false;
			boolean edge23 = false;
			boolean edge13 = false;
			
			Cell edges = this.getEdgeCell();
			//cycle through edges and count occurences of that node
			for(int k = 0; k < edges.size(); k++){
				BitVector edge = edges.getPA()[k];
				if(edge.contains(node1) && edge.contains(node2)){
					edge12 = true;
				}
				if(edge.contains(node2) && edge.contains(node3)){
					edge23 = true;
				}
				if(edge.contains(node1) && edge.contains(node3)){
					edge13 = true;
				}
			}
			
			if(edge12 && edge23 && edge13){
				Set<BitVector> triangle = new HashSet<BitVector>();
				triangle.add( new BitVector(node1) );
				triangle.add( new BitVector(node2) );
				triangle.add( new BitVector(node3) );
				allTriangles.add(triangle);
			}
			
		}
		//no triangles found
		if(allTriangles.isEmpty()){
			return this;
		}
		
		//now take list of triangles and add edges
		Iterator<Set<BitVector>> triangles = allTriangles.iterator();
		while(triangles.hasNext()){
			Set<BitVector> aTriangle = triangles.next();
			Iterator<BitVector> trianglesNodes = aTriangle.iterator();
			
			int node1 = trianglesNodes.next().getNumber();
			int node2 = trianglesNodes.next().getNumber();
			int node3 = trianglesNodes.next().getNumber();
			
			//add two new nodes in between node 2 and 3

			//add two new nodes
			int newNode1 = lastNode*2;
			int newNode2 = newNode1*2;
			this.addTwoNodes();
			
			//remove edge from node2 -> node3
			this.removeEdge(new BitVector(node2 + node3) );
			
			//add edge from new1 -> new2
			this.addEdge(new BitVector( newNode1 + newNode2 ));
			//add edges from port 1 to new1
			this.addEdge(new BitVector( node2 + newNode1) );
			//and edge from port 2 to new1
			this.addEdge(new BitVector( node3 + newNode2) );

		}
		if (GraphtoCell.makeCell(this).equalsNoPorts(cell)) {
			return this;
		}
		else{
			return null;
		}
	}

	public boolean isDisjoint() {
		BitVector[] edges = this.edgeCell.getPA();
		//holds whether we already visited this node
		HashMap<BitVector, Boolean> reached = new HashMap<BitVector, Boolean>();
		int numberReached = 0;
		
		//start searching at node 1
		BitVector first = new BitVector(1);
		LinkedList<BitVector> openList = new LinkedList<BitVector>();
		openList.add(first);
		
		while( !openList.isEmpty() ){
			
			BitVector parent = openList.pop();
			reached.put( parent, true );
			numberReached++;
			
			//search all edges
			for(BitVector edge : edges){
				//if edges goes from this node to another
				if(edge.contains( parent.getNumber() ) ){
					//find out who another is
					BitVector another = new BitVector( edge.getNumber() - parent.getNumber() );
					
					//if we haven't already been to another
					if( reached.get( another ) == null && !openList.contains(another) ){
						//add to openList
						openList.add(another);	
					}
				}
			}
		}
		
		if(numberReached == this.numNodes){
			return false;
		}
		return true;
		
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
	
	public String toString(){
		String name = this.name;
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
		return name + " " + edges;
		
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
	
	public BitVector getLastNode(){
		int lastNode = 1 << ( this.numNodes - 1 );
		return new BitVector( lastNode );
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

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public int compareTo(Graph arg0) {
		if(this.rank < arg0.rank){
			return -1;
		} else if(this.rank > arg0.rank){
			return 1;
		}
		return 0;
	}
}
