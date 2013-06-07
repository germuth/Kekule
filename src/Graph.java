import java.util.Set;

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
	 * I believe this number includes the ports
	 */
	private int numNodes;
	/**
	 * Adjacency matrix holding all the edges in this graph.
	 */
	private AdjacencyMatrix adjMatrix;
	//some variable to be declared here
	
	public Graph(String name, int numPorts, int numNodes, Set<String> edges){
		this.name = name;
		this.numPorts = numPorts;
		this.numNodes = numNodes;
		this.adjMatrix = new AdjacencyMatrix(numNodes, edges);
	}

}
