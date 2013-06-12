package bitvectors;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Adjacency Matrix
 * 
 * Means of representing which vertices of a graph are adjacent. As in, holds all the edges
 * within a graph. It is a two dimensional matrix of booleans, a true means there is an edge
 * from row to column. THere must be some node numbered row, and some node numbered column, 
 * which have an edge in one direction.
 * 
 * In this case, graphs are undirected, so for every edge x -> y, there is also an edge y -> x. 
 * This could be implemented more efficient using a mapping function to a upper triangular matrix
 * since the matrix will always be symmetric and have 0s on the diagonal. 
 * 
 * @author Aaron
 *
 */
public class AdjacencyMatrix {

	/**
	 * The matrix of booleans represented what edges exist in the graph
	 */
	private boolean[][] adjMatrix;
	
	/**
	 * Constructor for adjacency matrix. Parameter is array of strings of following form
	 * 0 1, 1 2, 2 3, 0 3 
	 * This means edge from 0 -> 1, 1 -> 2, however edges are bi-directional
	 * @param edges
	 */
	public AdjacencyMatrix(int numberOfNodes, Set<String> edges){
		
		this.adjMatrix = new boolean[numberOfNodes][numberOfNodes];
		Scanner edgeScanner = null;
		
		for(String x : edges){
			edgeScanner = new Scanner(x);
			int firstNode = edgeScanner.nextInt();
			
			int secondNode = edgeScanner.nextInt();
			
			//add one way
			this.adjMatrix[firstNode][secondNode] = true;
			//add opposite way
			this.adjMatrix[secondNode][firstNode] = true;
			
		}
		
		edgeScanner.close();
	}

	/**
	 * Returns a set of bit vectors representing all the edges in the graph. Each bitvector
	 * has precisely two zeros, indicated the two nodes that are connected. 
	 * For example, 12 or 0000 1100 (binary) means node 3 and 4 are connected
	 * @return above
	 */
	public Set<BitVector> getAllEdges() {
		Set<BitVector> allEdges = new HashSet<BitVector>();
		
		for(int i = 0; i < this.adjMatrix.length; i++){
			for(int j = i; j < this.adjMatrix[i].length; j++){
				if(this.adjMatrix[i][j]){
					//turn each node number into bit vector (<<)
					//the add together to get bit vector of two nodes
					//each node in the edge
					allEdges.add(new BitVector(
							(1 << i) + 
							(1 << j) ));
				}
			}
		}
		return allEdges;
	}
	
	public boolean[][] getAdjMatrix() {
		return adjMatrix;
	}

	public void setAdjMatrix(boolean[][] adjMatrix) {
		this.adjMatrix = adjMatrix;
	}
}
