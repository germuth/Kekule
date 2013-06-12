package bitvectors;
import java.util.Set;
import java.util.TreeSet;

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
	 * Set containing the nodes which are ports
	 */
	private Set<Integer> portSet;
	/**
	 * Adjacency matrix holding all the edges in this graph.
	 */
	private AdjacencyMatrix adjMatrix;
	
	/**
	 * Constructor with default ports {0,1,2,3...}
	 * @param name
	 * @param numPorts
	 * @param numNodes
	 * @param edges
	 */
	public Graph(String name, int numPorts, int numNodes, Set<String> edges){
		this.name = name;
		this.numPorts = numPorts;
		this.numNodes = numNodes;
		this.adjMatrix = new AdjacencyMatrix(numNodes, edges);
		
		this.portSet = new TreeSet<Integer>();
		for(int i = 0; i < numPorts; i++){
			this.portSet.add(i);
		}
	}
	/**
	 * Constructor with custom ports
	 * @param name
	 * @param numPorts
	 * @param numNodes
	 * @param edges
	 * @param ports
	 */
	public Graph(String name, int numPorts, int numNodes, Set<String> edges, Set<Integer> ports){
		this.name = name;
		this.numPorts = numPorts;
		this.numNodes = numNodes;
		this.adjMatrix = new AdjacencyMatrix(numNodes, edges);
		
		this.portSet = ports;
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
	
	/**
	 * Cycle through all edges of the Adjacency Matrix
	 * and return a set of bitvectors representing the deges
	 * @return, edges in bitVector form
	 */
	public Set<BitVector> getEdges(){
		return this.adjMatrix.getAllEdges();
	}

	public String getName() {
		return name;
	}

	public int getNumPorts() {
		return numPorts;
	}
	
	public AdjacencyMatrix getAdjMatrix() {
		return adjMatrix;
	}
}
