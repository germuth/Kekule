import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * Used to store all the edges in a graph. Graphs are undirected so this could be implemented
 * as symmetrix matrix eventually.
 * @author Aaron
 *
 */
public class AdjacencyMatrix {

	/**
	 * The matrix
	 */
	private boolean[][] adjMatrix;
	
	/**
	 * Constructor for adjacency matrix. Parameter is array of strings of following form
	 * 0 1, 1 2, 2 3, 0 3 etc.
	 * @param edges
	 */
	public AdjacencyMatrix(int numberOfNodes, Set<String> edges){
		
		this.adjMatrix = new boolean[numberOfNodes][numberOfNodes];
		Scanner edgeScanner = null;
		
		for(String x : edges){
			edgeScanner = new Scanner(x);
			int firstNode = edgeScanner.nextInt();
			
			int secondNode = edgeScanner.nextInt();
			
			this.adjMatrix[firstNode][secondNode] = true;
			this.adjMatrix[secondNode][firstNode] = true;
			
		}
		
		edgeScanner.close();
	}

	public Set<EdgePair> getAllEdges() {
		Set<EdgePair> allEdges = new TreeSet<EdgePair>();
		
		for(int i = 0; i < this.adjMatrix.length; i++){
			for(int j = i; j < this.adjMatrix[i].length; j++){
				if(this.adjMatrix[i][j]){
					allEdges.add(new EdgePair(i,j));
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