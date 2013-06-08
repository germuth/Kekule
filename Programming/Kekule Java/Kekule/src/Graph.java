import java.util.Set;
import java.util.TreeSet;

/**
 * In Kekule theory, a graph is characterized by the number of ports (cP), the number of nodes (cN), and 
 * the sequence of undirected edges between the nodes. cP <= cN <= 32. 
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
	
	public Set<Integer> getNodeSet(){
		Set<Integer> nodes = new TreeSet<Integer>();
		for(int i = 0 ; i < this.numNodes; i++){
			nodes.add(i);
		}
		return nodes;
	}
	
	public Set<EdgePair> getAllEdges(){
		return this.adjMatrix.getAllEdges();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumPorts() {
		return numPorts;
	}

	public void setNumPorts(int numPorts) {
		this.numPorts = numPorts;
	}

	public int getNumNodes() {
		return numNodes;
	}

	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}

	public Set<Integer> getPortSet() {
		return portSet;
	}

	public void setPortSet(Set<Integer> portSet) {
		this.portSet = portSet;
	}

	public AdjacencyMatrix getAdjMatrix() {
		return adjMatrix;
	}

	public void setAdjMatrix(AdjacencyMatrix adjMatrix) {
		this.adjMatrix = adjMatrix;
	}
	
}