package graphs;

import geneticAlgorithm.GeneticAlgorithm;
import gui.GraphToSMILES;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

import shared.BitVector;
import shared.Cell;
import shared.GraphtoCell;
import shared.PowerSet;
import shared.Utils;


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
 * Above is CORRECT
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
	 * Fitess value given to this graph based on the Genetic algorithms fitness
	 * function
	 */
	private double fitness;
	/**
	 * Cell containing all the edges of this graph in 
	 * bitvector form
	 */
	private Cell edgeCell;
	
	// take in graph from input
	public Graph(String name, int nP, int nC, Set<String> edges) {
		
		if( nC >= 31){
			System.err.println("31 Nodes Reached");
			return;
		}
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
		this.fitness = 0;
	}
	
	public Graph(String name, int nP, int nC, Cell edges){
		this.numPorts = nP;
		this.numNodes = nC;
		if( this.numNodes >= 31){
			System.err.println("31 Nodes Reached");
		}
		this.edgeCell = edges;
		this.name = name;
		this.fitness = 0;
	}
	
	public Graph(int nP, int nC, Cell edges){
		this.numPorts = nP;
		this.numNodes = nC;
		if( this.numNodes >= 31){
			System.err.println("31 Nodes Reached");
		}
		this.edgeCell = edges;
		this.name = "";
		this.fitness = 0;
	}
	
	public Graph(Graph g){
		this.name = g.name;
		this.numNodes = g.numNodes;
		if( this.numNodes >= 31){
			System.err.println("31 Nodes Reached");
		}
		this.numPorts = g.numPorts;
		this.edgeCell = new Cell(g.edgeCell);
		this.fitness = g.fitness;
	}
	
	
	/**
	 * equals method. Does not compare based on port number, only edges
	 */
	@Override
	public boolean equals(Object obj) {
		Graph another = (Graph) obj;
		this.edgeCell.sortBySize();
		another.edgeCell.sortBySize();
		if(this.edgeCell.equals(another.edgeCell)){
			return true;
		}
		return false;
	}

	/**
	 * Adds an edge to this graph
	 * @param bv, BitVector representation of the edge you wish to add
	 */
	public void addEdge(BitVector bv){
		this.edgeCell.add(bv);
	}
	
	/**
	 * if this edge is
	 * (1) already contained in this graph
	 * (2) is an invalid edge (doesn't reach two nodes)
	 * (3) increase the degree on a node too high
	 * it is deemed a 'bad edge' and true will be returned
	 * otherwise, false
	 * @param edge
	 * @return true/false based on abve
	 */
	public boolean isBadEdge( BitVector edge){
		//if edge goes to itself..
		if( edge.getNumber() - edge.firstBit() == 0){
			return true;
		}
		//if we already have that edge
		if( this.edgeCell.contains( edge ) ){
			return true;
		}
		//if it makes degree too high
		Graph temp = new Graph(this);
		temp.addEdge(edge);
		if( temp.getHighestDegree() > 3 || temp.getHighestPortDegree() > 2){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks the graph for a bad cycles. 
	 * Bad cycles are defined as when a graph has 2 cycles which
	 * share more than one edge (or more than two nodes). This is 
	 * Infeasible in carbon chemistry and so is used to decrement
	 * the fitness of the graph
	 */
	public boolean hasBadCycles(){
		ArrayList<ArrayList<BitVector>> allCycles = this.getAllCycles();
		//iterate through all pairs of cycles
		//if any two have intersection greater than 2
		//this graph indeed has bad cycles
		for(int i = 0; i < allCycles.size(); i++){
			ArrayList<BitVector> cycle1 = allCycles.get(i);
			
			for(int j = i + 1; j < allCycles.size(); j++){
				ArrayList<BitVector> cycle2 = new ArrayList<BitVector>( allCycles.get(j) );
				
				//cycle 2 becomes intersection
				cycle2.retainAll( cycle1 );
				
				if( cycle2.size() > 2){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns the number of cycles in this graph. Does this by obtaining a spanning tree
	 * and counting the number of edges that we're removed
	 */
	public int getNumCycles() {
		// find spanning tree by DFS-ing the nodes
		Graph spanningTree = GraphToSMILES.getSpanningTree( this );

		return ( this.edgeCell.size() - spanningTree.edgeCell.size() );
		
	}

	/**
	 * Uses the 'flooding' algorithm to find all cycles in a graph. Excludes 
	 * so called 'super cycles' which contain all the nodes of two smaller
	 * cycles. 
	 * 
	 * start with packet at every node
	 * at every opportunity, send packet to all your neighbors
	 * except pipe you already sent it down
	 * if at any point you reach node you started at, add to cycles
	 * if at any point, packet length exceeds maximum, remove packet
	 * should find all cycles below max length
	 * then parse through and eliminate super cycles
	 * if union of two cycles equals any other cycles, remove that cycle
	 * return list if cycles
	 */
	public ArrayList<ArrayList<BitVector>> getAllCycles(){
		ArrayList<ArrayList<BitVector>> allCycles = new ArrayList<ArrayList<BitVector>>();
		
		
		LinkedList<ArrayList<BitVector>> openList = new LinkedList<ArrayList<BitVector>>();
		
		//can detect all cycles by only starting packets at nodes with degree 3
		for(int i = 1; i <= this.getLastNode().getNumber(); i *= 2){
			BitVector currentNode = new BitVector(i);
			if( this.getDegree( currentNode) == 3){
				ArrayList<BitVector> packet = new ArrayList<BitVector>();
				packet.add(currentNode);
				openList.add( packet );
			}
		}
		//if no nodes with degree of 3, then either the graph contains no cycles, or 
		//the graph is one giant cycle, so we add random node
		if( openList.isEmpty() ){
			BitVector currentNode = new BitVector( 1 );
			ArrayList<BitVector> packet = new ArrayList<BitVector>();
			packet.add(currentNode);
			openList.add( packet );
		}
		while( !openList.isEmpty() ){
			
			ArrayList<BitVector> packet = openList.pop();
			BitVector parent = packet.get( packet.size() - 1);
			
			//if packet.end == packet.front
			//we have a cycle
			//ensure cycle is at least length 3
			if( packet.get(0) .equals( packet.get( packet.size() - 1) ) && packet.size() > 2){
				packet.remove( packet.size() - 1 );
				allCycles.add( packet );
				continue;
			}
			
			//search all neighbors
			ArrayList<BitVector> neighbours = this.getAllNeighbours( parent );
			for(BitVector buddy : neighbours){
				
				//don't send packet back to the neighbor that sent it to you
				if( packet.size() >= 2 && buddy.equals( packet.get( packet.size() - 2) ) ){
					continue;
				}
				
				//prevents cycles within cycles
				//5 -> 6 -> 7 -> 8 -> 6 -> 5 is not valid cycle
				//shouldn't be allowed to add node we already have in list,
				//unless that node is the starting node and we have found a legitimate cycle
				//if we already contain that node, and that node isn't the starting point, 
				//don't go this way
				if( packet.contains( buddy ) && !packet.get(0).equals( buddy )){
					continue;
				}
				
				//send packet everywhere else
				//as long as packet is below max length
				if( packet.size() < 12){
					ArrayList<BitVector> newPacket = new ArrayList<BitVector>(packet);
					newPacket.add( buddy );
					openList.add( newPacket );
				}
				
			}
		}
		
		//if list empty
		if( allCycles.isEmpty() ){
			return allCycles;
		}
		//sort all cycles, so identical ones can be identified
		for(int i = 0; i < allCycles.size(); i++){
			Collections.sort( allCycles.get(i) );
		}
		//remove identical cycles
		allCycles = Utils.deleteDuplicates(allCycles);
		
		//remove super cycles
		allCycles = removeSuperCycles( allCycles );
		
		return allCycles;
	}
	
	/**
	 * Parses through a list of cycles, and removes super cycles.
	 * Super cycles are larger cycles that are made up of two combined cycles
	 */
	public ArrayList<ArrayList<BitVector>> removeSuperCycles( ArrayList<ArrayList<BitVector>> allCycles ){
		//for each cycle
		//iterate backwards to get the larger cycles first
		for(int i = 0; i < allCycles.size(); i++){
			if( allCycles.get(i) == null){
				continue;
			}
			ArrayList<BitVector> currentCycle = allCycles.get(i);
			
			//if this one of the other cycles is a subset of this cycle, then 
			//this cycle is not chord-less, and should be removed 
			
			for(int j = 0; j < allCycles.size(); j++){
				if( allCycles.get(j) == null ||
						i == j){
					continue;
				}
				ArrayList<BitVector> cycle2 = allCycles.get(j);
				
				if( isSubSet(cycle2, currentCycle)){
					allCycles.set(i, null);
				}
			}
			
			//THIS IS PRINTING THAT WE HAVE NO CYCLES!!!
		}

		allCycles = Utils.removeNulls( allCycles );
	
		return allCycles;
	}
	
	//TODO make a lot of methods private
	/**
	 * Tests whether nextCycle is a subset of currentCycle. If Current cycle is a sub
	 * set of next cycle, unless equal.
	 * 
	 * @param nextCycle
	 * @param currentCycle
	 * @return
	 */
	private boolean isSubSet(ArrayList<BitVector> nextCycle, ArrayList<BitVector> currentCycle){
		
		if( nextCycle.size() > currentCycle.size() ){
			return false;
		}
		
		//iterate through nextCycle
		//if currentCycle doesn't contain all return false
		for(int i = 0; i < nextCycle.size(); i++){
			if( !currentCycle.contains( nextCycle.get(i)) ){
				return false;
			}
		}
		
		return true;
	}
	/**
	 * Removes an edge from this graph. 
	 * @param bv, BitVector representation of the edge you wist to remove
	 */
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
	
	/**
	 * Returns a list of BitVector nodes. Each node is a neighbour of the inputted node
	 */
	public ArrayList<BitVector> getAllNeighbours(BitVector bv){
		ArrayList<BitVector> allNeighbours = new ArrayList<BitVector>();
		
		for(int i = 0; i < this.getEdgeCell().size(); i++){
			BitVector current = this.getEdgeCell().getPA()[i];
			
			if( current.contains( bv.getNumber() )){
				allNeighbours.add( new BitVector( current.getNumber() - bv.getNumber() ));
			}
		}
		
		return allNeighbours;
	}
	
	/**
	 * Returns whether the inputted bitvector is a port of this graph
	 */
	public boolean isPort(BitVector bv){
		int nodeNum = bv.firstNode();
		if( this.numPorts - 1 >= nodeNum ){
			return true;
		}
		return false;
	}
	/**
	 * Gets the highest degree of any given node in this graph and returns
	 * in integer form. 
	 * 
	 * Graph is undirected so indegree == outdegree
	 * @return the highest degree of any given node in this graph
	 */
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
	
	/**
	 * Gets the degree of a given node. Nodes are entered in BitVector form
	 * @param bv, BitVector representation of node
	 * @return how many edges that node is present in
	 */
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
	
	/**
	 * Returns a set of all nodes in this graph that have a degree
	 * of 1
	 * @return
	 */
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
	
	/**
	 * Extends every port of this graph out one node, and replaces its former position with an 
	 * internal vertex. The interval vertex is bonded to everything the port was bonded to. The port
	 * is bonded to exclusively the internal vertex. 
	 * 
	 * This procedure can often be used to alleviate the degree on your ports, while maintaining
	 * the graph's cell. However, this procedure is not guaranteed to preserve the cell.
	 * 
	 * If the extended graph has a different cell, null is returned. Every port must be connected to the 
	 * graph (at least degree 1)
	 * @return
	 */
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
		if( !newCell.equals(cell) ){
			//extended = new Graph(this);
			return null;
		}
		extended.setName( extended.getName() + "Extended");
		return extended;
	}
	
	/**
	 * Extends every port of this graph out one node, and replaces its former position with an 
	 * internal vertex. The interval vertex is bonded to everything the port was bonded to. The port
	 * is bonded to exclusively the internal vertex. 
	 * 
	 * This procedure can often be used to alleviate the degree on your ports, while maintaining
	 * the graph's cell. However, this procedure is not guaranteed to preserve the cell.
	 * 
	 * This version will return the graph regardless of whether or not the new cell matches or not
	 * @return
	 */
	public Graph extendPortsNoCell() {
		// extended graph
		Graph extended = new Graph(this);

		int lastNode = extended.getLastNode().getNumber();
		// ports always 0 1 2 3 4
		for (int port = 1; port <= (1 << (extended.numPorts - 1)); port *= 2) {

			// add new node to replace port
			int newNode = lastNode * 2;
			lastNode = newNode;
			extended.numNodes++;

			// find all edges which connect to extended port
			for (int i = 0; i < extended.edgeCell.size(); i++) {
				BitVector edge = extended.edgeCell.getPA()[i];
				if (edge.contains(port)) {

					int otherNode = edge.getNumber() - port;

					// delete edges from port to other
					extended.removeEdge(edge);
					// add edge from port to other
					extended.addEdge(new BitVector(otherNode + newNode));
				}
			}

			extended.addEdge(new BitVector(newNode + port));
		}

		extended.getEdgeCell().sortBySize();
		extended.setName(extended.getName() + "Extended");
		return extended;
	}
	
	/**
	 * In the genetic algorithm, it's possible that it creates two new nodes, 
	 * and connects them to eachother, but nothing else. This is disjoint, 
	 * which is not allowed. If the two verticies are not ports, we can
	 * simply remove them, which is done here. If they are ports, we must
	 * connect them, which is done in connect()
	 * 
	 * TODO
	 * Right now it only checks the last Two Nodes, which are most likely
	 * to have this situation occur
	 */
	public void trimDisjoint(){
		GeneticAlgorithm.calculateFitness( this );
		double beginFitness = this.getFitness();
	
		BitVector lastNode = this.getLastNode();
		BitVector secondLast = new BitVector( lastNode.getNumber() / 2 );
		
		//make sure they aren't ports
		if( this.numNodes < this.numPorts + 2){
			return;
		}
		
		//if they are only connected to one thing
		if( this.getDegree(lastNode) == 1 && this.getDegree(secondLast) == 1){
			//and that thing happens to be each other
			BitVector edge = new BitVector( lastNode.getNumber() + secondLast.getNumber() );
			if(this.edgeCell.contains( edge )){
				this.removeEdge( edge );
				this.setNumNodes( this.numNodes - 2 );
			}
		}
		
		GeneticAlgorithm.calculateFitness( this );
		if( this.getFitness() != beginFitness){
			System.err.println("Trim Disjoint is broken yo");
		}
	}

	/**
	 * Turns this graph from a disjoint graph to a connected graph. Only
	 * works in the case of a graph split into two sections. 
	 * 
	 * Only returns the new graph if it's cell is the same as the old one, although
	 * using it's current procedure I believe this will always be the case. 
	 * 
	 * Connects them by adding two interval vertices. One of the vertices is connected to 
	 * a node on each half of the graph. The other internal vertex is attached only to the 
	 * other vertex. This means they must alwasy share a double bond, meaning the 
	 * bonds added to connect the graphs can never be used for a double bond and the cell
	 * is alwasy the same (probably).
	 * 
	 * (This) the graph, is not edited, a new one is produced off of it.
	 * @param cell
	 * @return a new Graph which is connected, or null if procedure failed
	 */
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
				if(GraphtoCell.makeCell(connected).equals(cell) ){
					connected.name += "Connected";
					return connected;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Gets all cycles of this graph, and turns and 4 cycles into 6 cycles, and
	 * any 3 cycles into 5 cycles. This fits carbon chemistry much better. Checks to see if 
	 * the cell or fitness changed, if it did, it writes errors to console.
	 */
	public void widenCycles() {	
		//get cell and fitness before hand
		ArrayList<ArrayList<BitVector>> allCycles = this.getAllCycles();
		
		//spots where we add nodes to alleviate small cycles
		ArrayList<ArrayList<BitVector>> fixingSpots = new ArrayList<ArrayList<BitVector>>();
		
		for(int i = 0; i < allCycles.size(); i++){
			ArrayList<BitVector> currentCycle = allCycles.get(i);
			
			//if square or triangle
			if( currentCycle.size() == 4 || currentCycle.size() == 3){
				ArrayList<BitVector> fixingSpot = this.getTwoNodesFromCycle(currentCycle, allCycles);
				if( ! fixingSpot.isEmpty() ){
					fixingSpots.add( fixingSpot );
				}
				
			}	
		}
		
		//go through each fixing spot and actually fix
		for(int i = 0; i < fixingSpots.size(); i++){
			ArrayList<BitVector> fixingSpot = fixingSpots.get(i);
			
			int node1 = fixingSpot.get(0).getNumber();
			int node2 = fixingSpot.get(1).getNumber();
			int lastNode = this.getLastNode().getNumber();

			// add two new nodes
			int newNode1 = lastNode * 2;
			int newNode2 = newNode1 * 2;
			this.addTwoNodes();

			// remove edge from node1 -> node2
			this.removeEdge(new BitVector(node1 + node2));

			// add edge from new1 -> new2
			this.addEdge(new BitVector(newNode1 + newNode2));
			// add edge from node1 to new1
			this.addEdge(new BitVector(node1 + newNode1));
			// and edge from node2 to new1
			this.addEdge(new BitVector(node2 + newNode2));
		}
		
	}
	
	/**
	 * Given all cycles, and one specific one, it finds two nodes of that cycle which are both
	 * not located in any other cycle. If this is impossible, an empty list is returned 
	 * 
	 * TODO use methods here rather than like 20 break statements
	 */
	private ArrayList<BitVector> getTwoNodesFromCycle(ArrayList<BitVector> theCycle, ArrayList<ArrayList<BitVector>> allCycles){
		ArrayList<BitVector> currentCycle = theCycle; 
		
		BitVector finalNode1 = null;
		BitVector finalNode2 = null;
		
		//if theCycle is the only cycle in the graph
		//than any two adjacent nodes will work
		if( allCycles.size() == 1 ){
			
			loop:
			//grab first two from cycle who are connected
			for(int i = 0; i < theCycle.size(); i++){
				BitVector node1 = theCycle.get(i);
				for(int j = i + 1; j < theCycle.size(); j++){
					BitVector node2 = theCycle.get(j);
					
					BitVector edge = new BitVector( node1.getNumber() +
							node2.getNumber() );
					if( this.getEdgeCell().contains( edge )){
						finalNode1 = node1;
						finalNode2 = node2;
						break loop;
					}
				}
			}
		} 
		// more than one cycle in graph
		else {
			// find any two adjacent nodes of cycle which aren't both in the
			// same cycle
			// try all pairs one works\
			nodeSelection:
			for (int j = 0; j < currentCycle.size(); j++) {
				BitVector node1 = currentCycle.get(j);
				nodeSelectionIn:
				for (int k = j + 1; k < currentCycle.size(); k++) {
					BitVector node2 = currentCycle.get(k);

					// make sure node1 and node2 connected in our cycle
					if (this.edgeCell.contains(new BitVector(node1.getNumber()
							+ node2.getNumber()))) {
						//there must be no other cycle which contains this edge
						
						
						// for each other cycle
						for (int l = 0; l < allCycles.size(); l++) {
							// exclude current Cycle
							if (allCycles.get(l).equals(currentCycle)) {
								continue;
							}
							
							//if cycle contains both nodes
							if (allCycles.get(l).contains(node1)
									&& allCycles.get(l).contains(node2)) {
								//this is not good
								break nodeSelectionIn;
							} 

						}
					
						//made it to the end
						finalNode1 = node1;
						finalNode2 = node2;
						break nodeSelection;

					}
						}	
				} 	
		}
		
		ArrayList<BitVector> spot = new ArrayList<BitVector>();
		//if two nodes were found
		if( finalNode1 != null){
			spot.add( finalNode1 );
			spot.add( finalNode2 );
		}
		return spot;
	}

	/**
	 * Checks whether this graph is planar. Uses Euller statements below.
	 * However, not all graphs in which that condition holds are necessarily
	 * planar. Planarity test algorithms are needed for an absolute answer.
	 * 
	 * For a simple, connected, planar graph with v vertices and e edges, the
	 * following simple planarity criteria hold: 
	 * Theorem 1. If v >= 3 then e <= 3v - 6; 
	 * Theorem 2. If v > 3 and there are no cycles of length 3, then e <= 2v - 4.
	 * 
	 * TODO not currently corect
	 * but it's not like it was useful anyway
	 * 
	 * @return whether this graph is probably planar or not
	 */
	public boolean isPlanar(){
		int numEdges = this.edgeCell.size();
		
		//first condition
		if( numEdges <= 3*this.numNodes - 6 ){
			
			//more than three nodes
			if( numNodes > 3){
				//second condition
				//if( this.isTriangleFree() && numEdges <= 2*this.numNodes - 4){
					return true;
				//}
				//failed second condition
				//else{
				//	return false;
				//}
			} 
			//meets first condition and has less than three nodes
			else{
				return true;
			}
		}
		//failed first condition
		return false;
	}

	/**
	 * Checks whether this graph is disjoint. Does this by breadth-first-searching the graph and counting
	 * how many different nodes it reaches. If not all nodes are reached, false is returned, otherwise true.
	 * @return whether this (graph) is disjoint or not (as in connected)
	 */
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
	
	/**
	 * Iterates through each port is this graph and checks the degree. Returns the 
	 * highest degree found. In order to model real molecules, the degree on all ports
	 * should be at most 2
	 * @return the largest degree of all ports of this graph
	 */
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
			int k = bv.firstBit();
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
	
	/**
	 * Translates a graph's Cell of bitVectors into a visual representation
	 * of the edges. Each edge is translated in the following way. 
	 *  0101 = edge from 0 - 2
	 * 11000 = edge from 3 - 4
	 * 
	 * The name, #Nodes, and #Ports of the graph is also printed.
	 */
	public void writeGraph(){
		String title = "";
		if(this.name != null){
			if(this.isPlanar()){
				title += this.name + "_P: ";
			}
			else{
				title += this.name + ": ";
			}
		}
		title += this.numNodes +" Nodes, " + " " + this.numPorts + " Ports";
		System.out.println(title);
		String edges = "Edges: ";
		for(int i = 0; i < this.edgeCell.size(); i++){
			BitVector edge = this.edgeCell.getPA()[i];
			int p = edge.firstNode();
			edge = new BitVector( edge.getNumber() - ( 1 << p ) );
			int q = edge.firstNode();
			edges += ( p ) + "-" + ( q );
			
			if(i != this.edgeCell.size() - 1){
				edges += ", ";
			}
		}
		System.out.println(edges);
	}
	
	/**
	 * Returns the last port of this graph in bitvector form, 
	 * if 5 ports, lastPort = 0001 0000
	 */
	public BitVector getLastPort(){
		int lastPort = 1 << (this.numPorts - 1);
		return new BitVector(lastPort);
	}
	

	/**
	 * Returns the last node of this graph in bitvector form
	 * if 5 nodes, lastNode = 10000
	 * @return lastNode in bitvector form
	 */
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
		if(this.numNodes >= 31){
			System.err.println("31 Nodes Reached");
		}
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
		if( this.numNodes >= 31){
			System.err.println("31 Nodes Reached");
		}
	}
	
	public void setName(String name2) {
		this.name = name2;	
	}
	
	/**
	 * Appends inputed string to this graphs name
	 * 
	 * this.name = this.name + input;
	 * @return
	 */
	public void appendName(String suffix){
		this.name += suffix;
	}
	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	/**
	 * Compares two graphs based on their fitness. Used in population to keep
	 * the list of graphs in sorted order.
	 */
	@Override
	public int compareTo(Graph arg0) {
		if(this.fitness < arg0.fitness){
			return 1;
		} else if(this.fitness > arg0.fitness){
			return -1;
		}
		return 0;
	}
}
