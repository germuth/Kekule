package makeGraph;

import graphs.ClassifyGraph;
import graphs.Graph;
import graphs.TemplateMolecule;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import makeCell.GraphtoCell;
import shared.BitVector;
import shared.Cell;
import shared.InputParser;
import shared.Utils;

/**
 * This class reads a classification of all Kekule cells of a port number and attempts
 * to find graphs for them.
 * 
 * @author Aaron
 */
public class CellToGraph {
	private static File f = new File("myraw.txt");
	private static Scanner fileScanner;
	private static int rank;
	private static String name;
	
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	/*
	public static void main(String[] args) {
		
		Cell input = null;
		try{
			fileScanner = new Scanner(f);
			readTitle();
			input = readCell();
		} catch(Exception e){
			System.out.println("error reading from file");
			System.exit(0);
		}
		
		//construct matched graphs
		//how many internal vertices are allowed
		int internal = 6;
		ClassifyGraph.setAMG(internal);
		Histogram.rank = rank;
		Permutations.freePerm();
		
		while(input != null){
			
			ArrayList<Graph> allGraphs = findGraph(rank, internal, input);
			for(int i = 0; i < allGraphs.size(); i++){
				Graph g = allGraphs.get(i);
				g.getEdgeCell().removeDuplicates();
				if(g.getHighestDegree() > 4){
					allGraphs.set(i, null);
				}
			}
			
			allGraphs = Utils.removeNulls(allGraphs);
			
			if( !allGraphs.isEmpty() ){
				allGraphs = Utils.deleteDuplicatesGraph(allGraphs);
			}
			
			for(int i = 0; i < allGraphs.size(); i++){
				Graph g = allGraphs.get(i);
				
				if(g != null){
					g.setName(name);
					g.minimizeGraph();
					g.writeGraph();
					
					//try to find cell from graph
					//and make sure they match up
					//with compareL
				}
			}
			
			try{
				input = readCell();
			} catch(Exception e){
				input = null;
			}
		}

	}
	*/
	public static ArrayList<Graph> tryTemplateMolecules(Cell cell){
		ArrayList<Graph> allGraphs = new ArrayList<Graph>();
		
		try {		
			//read templates from txt
			File f = new File("TemplateMolecules.txt");
			Scanner fScanner = new Scanner(f);
			
			TemplateMolecule input = InputParser.readTemplateMolecule(fScanner, cell.getNumPorts());
			//get all graphs
			while(input != null){
				allGraphs.addAll( input.getAllGraphs( cell.getNumPorts() ) );
				
				try{
					input = InputParser.readTemplateMolecule(fScanner,  cell.getNumPorts() );
				} catch(NoSuchElementException e){
					input = null;
				}
			}
			
			//test all graps
			for(int i = 0; i < allGraphs.size(); i++){
				Graph current = allGraphs.get(i);
				
				Cell c = GraphtoCell.makeCell(current);
				c.normalize();
				current.getEdgeCell().sortBySize();
				if( !c.equalsNoPorts(cell) ){
					allGraphs.set(i, null);
				}
			}
			
			allGraphs = Utils.removeNulls(allGraphs);
			
		} catch (FileNotFoundException e) {
			return allGraphs;
		}
		
		return allGraphs;
	}
	
	private static ArrayList<Graph> getPreloadedGraphs(){
		ArrayList<Graph> preloaded = new ArrayList<Graph>();
	
		//Benzene
		
		//Napthalene
		
		//
		
		return preloaded;
	}
	
	//removes disjoint graphs and tries to add connected versions
	public static void removeDisjoint(ArrayList<Graph> allGraphs, Cell cell){
		
		for(int i = 0; i < allGraphs.size(); i++){
			Graph g = allGraphs.get(i);
			if(g == null){
				continue;
			}
			if( g.isDisjoint() ){
				//if new graph found, it replaces old non-connected one
				//if no new connected graph found, null is placed there
				Graph connected = g.connect(cell);
				allGraphs.set(i, connected);
			}
		}
	}
	
	public static void removeHighDegree(ArrayList<Graph> allGraphs){
		for(int i = 0; i < allGraphs.size(); i++){
			Graph g = allGraphs.get(i);
			g.getEdgeCell().removeDuplicates();
			if(g.getHighestDegree() > 3 || g.getHighestPortDegree() > 2){
				allGraphs.set(i, null);
			}
		}
		
		allGraphs = Utils.removeNulls(allGraphs);
		
		if( !allGraphs.isEmpty() ){
			allGraphs = Utils.deleteDuplicatesGraph(allGraphs);
		}

	}

	public static void widenCycles(ArrayList<Graph> allGraphs, Cell cell) {
		for (int i = 0; i < allGraphs.size(); i++) {
			Graph g = allGraphs.get(i);
			if(g == null){
				continue;
			}
			allGraphs.set(i, g.triangleFree(cell) );
		}

	}
	
	/**
	 * TODO these comments Assume P = U K, as in K is flexible (K contains every
	 * port in P)
	 * 
	 * Monotonic means preserves order. for example, if x < y, then f(x) < f(y)
	 * 
	 * K is monotonic in the sense that if E0 subset E, then K(V, E0) subset
	 * K(V, E)
	 * 
	 * Therefore, given a K, we can begin searching for a graph with K(V, E0)
	 * and then add edges to E0.
	 * 
	 * The growth of K(V, E) when E grows is:
	 * 
	 * K(G) = K(V, E0) U ( (e U P) symDif K(U, E1) )
	 * 
	 * e is edge of E. E0 = E \ e U = V \ e E1 = E intersection U(2)
	 * 
	 * 
	 * choose a port assignment where bc(k symDif K) is largest. K' = k symDif K
	 * is regular. If we can find graph G' for K', we can then use backwards
	 * translation (k symDif G') == G
	 * 
	 * @param rank
	 * @param internal
	 * @param cell
	 * @return
	 */
	public static ArrayList<Graph> findGraph(int rank, int internal, Cell cell) {

		ArrayList<Graph> answer;// = new ArrayList<Graph>();

		// tries the best border graph
		answer = findGraphBEG(rank, internal, cell);

		// if failed to find best border, search for graph using decomposition
		int i = 0;

		Cell p1 = null;
		Cell p2 = null;

		// nf = not Finished
		boolean nf = true;
		while (nf && i < cell.size()) {
			BitVector pa = cell.getPA()[i];
			Cell other = new Cell(cell);
			other.translate(pa);
			try{
				if ( !other.indecomposable(p1, p2) ) {
					ArrayList<Graph> l1 = findGraphBEG(rank, internal, p1);
					ArrayList<Graph> l2 = findGraphBEG(rank, internal, p2);

					for (int j = 0; j < l1.size(); j++) {
						Graph g1 = l1.get(j);
						for (int k = 0; k < l2.size(); k++) {
							Graph g2 = l2.get(k);
							if (g1 != null && g2 != null) {
								nf = false;
								g1 = Graph.oDot(g1, g2);
								g1.translate(pa);
								System.out.println("AOEAOEAOEAOEOAEAOEOAEOAE");
								answer.add(g1);
							}
						}

					}
				}
			} catch(Exception e){
				
			}
			i++;
		}

		return answer;
	}
	
	/**
	 * Finds the Best Border Graph and then uses to find the actual graph for cell. First 
	 * translates cell to discover the best border graph and then once the graph is found
	 * for translation, the graph is re-translated back to the original. 
	 * @param rank, the number of ports involved
	 * @param internal, the maximum number of internal vertices allowed
	 * @param cell, the cell we are trying to find a graph for
	 * @return a graph for the given cell
	 */
	private static ArrayList<Graph> findGraphBEG(int rank, int internal, Cell cell) {
		
		ArrayList<Graph> answer = new ArrayList<Graph>();
		
		cell.sortBySize();

		for (int i = 0; i < cell.size(); i++) {
			BitVector y = cell.getPA()[i];
			Cell copy = new Cell(cell);
			copy.translate(y);

			ArrayList<Graph> temp = findGraphEG(rank, internal, copy);
			
			for (int j = 0; j < temp.size(); j++) {
				Graph g = temp.get(j);
				// sort
				g.getEdgeCell().sortBySize();
				
				//Translate graph back over x, to get original
				if(g != null){
					g.translate(y);
				}
				answer.add(g);
			}
			
			
		}
		// Finds graph for nc

		
		return answer;
		
	}
	
	/**
	 * TODO finish
	 * Cell must be regular, ie contain 0 as port assignment. 
	 * Therefore the subgraph of the internal nodes has a matching, we can
	 * use the classification of the matched graphs of rank (nodes - ports)
	 * 
	 * @param rank, the number of ports in cell
	 * @param internal, the number of internal vertices allowed
	 * @param cell, the cell we want a graph for
	 * @return Graph for cell
	 */
	private static ArrayList<Graph> findGraphEG(int rank, int internal, Cell cell){
		ArrayList<Graph> answers = new ArrayList<Graph>();
		
		//get the best Border Graph
		Graph g0 = borderGraph(rank, cell);	
		//determine cell of border graph
		//used to compare to full graph cell
		//as c0 becomes cell
		//g0 becomes g
		//and we have found the graph
		Cell c0 = GraphtoCell.makeCell(g0);
		
		//cells have equal size, we have found applicable graph
		if( c0.size() == cell.size()){
			answers.add(g0);
		}
		//if were not allowed to add more nodes, we must return null
		if( internal < 2 ){
			return answers;
		}
		//if we are here, g0 was not enough
		Graph g1 = new Graph(rank, rank, null);
		Graph answer = null;
		//while we haven't found a graph AND we are still allowed to 
		//add more nodes and keep searching
		while( g1.getNumNodes() + 2 <= rank + internal ){
			g1.addTwoNodes();
			System.out.println("...trying " + g1.getNumNodes() + " nodes...");
			
			//When we add internal vertices, we need to add internal edges so that the total
			//graph has a perfect matching or else Kekule state is lost
			//Get List of Isomorphism classes with perfect matchings and try recursive algorithm
			//on each
			//get classification for all matched graphs with rank (nodes - our rank)
			// nodes - rank = number of internal nodes
			//must add internal nodes which have perfect matching
			ArrayList<Cell> allMatchedGraphs = ClassifyGraph.getAMG(g1.getNumNodes() - rank);
			
			//Edge Set L
			Cell lEdges = potEdges(rank, g1.getNumNodes());
			lEdges.sortBySize();
			
			//for each classification
			for(int i = 0; i < allMatchedGraphs.size(); i++){
				//get current classification
				Cell current = allMatchedGraphs.get(i);
				//transfer edges from current classification to g1
				Cell edgeSet =  g0.getEdgeCell();
				for(int j = 0; j < current.size(); j++){
					int x = current.getPA()[j].getNumber();
					edgeSet.add( new BitVector( x << rank ) );
				}
				g1.setEdgeCell(edgeSet);
				
				
				//add edges to g1 until final graph found
				//using recursive procedure of 4.2 (Hesselink)
				answer = findGraphR(g1, c0, cell, lEdges, 0);
				
				//if answer found
				if(answer != null){
					Graph ans = new Graph(g1);
					answers.add(ans);
				}
			}
			
		}
		
		return answers;
	}
	
	/**
	 * Main Recursive Algorithm of Hesselink's Paper, Section 4.2
	 * 
	 * Adds Edges from lEdges to g0 in order to get g0's cell to match argument cell.
	 * c0 is g0's current cell. It slowly approximates cell. When this has happened,
	 * g0 now equals a graph for cell. lptr holds the current edge we are adding.
	 * 
	 * Preserves all arguments other than g0, which will be the answer at the end.
	 * Assumes c0 is a subset of cell, and c0 is a Kekule cell for g0.
	 * Returns null if no such graph is found
	 */
	private static Graph findGraphR(Graph g0, Cell c0, Cell cell, Cell lEdges, int lptr){
		
		//if cell size matches, we have found graph
		if( c0.size() == cell.size() ){
			return g0;
		}
		//if we got to the last edge, we failed to find graph
		if( lptr == lEdges.size()){
			return null;
		}
		Cell edgeCell = new Cell(g0.getEdgeCell());
		Graph result = findGraphR(g0, c0, cell, lEdges, lptr + 1);
		if(result != null){
			return result;
		}
		
		//re set g0's edges to what they used to be 
		//as they were edited in recursion
		g0.setEdgeCell(edgeCell);
		//get current edge we are going to add
		BitVector edge = lEdges.getPA()[lptr];

		//BitVector for the nodes
		BitVector nodeV = g0.getNodeVector();
		//all nodes other than edge
		BitVector uu = BitVector.symmetricDifference(nodeV, edge);
		//get cell for uu
		Set<BitVector> kekCell = GraphtoCell.makeCell(uu, g0);
		Cell kk = new Cell(kekCell, g0.getNumPorts() );
		
		//BitVector for the ports
		BitVector portV = g0.getPortVector();
		//BitVector of all ports plus nodes in edge that aren't ports
		//minus nodes in edge which are ports
		BitVector translation = BitVector.intersection(portV, edge);
		//translate above cell
		kk.translate(translation);
		
		if(kk.isSubSetOf(cell)){
			g0.addEdge(edge);
			Cell c1 = new Cell(c0);
			c1 = Cell.union(c1, kk);
			//try to add next edge
			result = findGraphR(g0, c1, cell, lEdges, lptr + 1);
			return result;
		}
		return null;
	}
	
	/**
	 * Constructs a list of all possible edges between all ports and the interval 
	 * Vertices which may be added. This is used to ensure when we add vertices to the
	 * graph it still has a perfect matching.
	 * No new internal edges are added. 
	 * @param rank, the number of ports
	 * @param cn, the number of nodes currently in the graph
	 * @return a Cell, holding the above list of edges
	 */
	private static Cell potEdges(int rank, int cn){
		Set<BitVector> answerSet = new HashSet<BitVector>();
		//int size = rank * (cn - rank); 
		int pEnd = 1 << rank;
		int qEnd = 1 << cn;
		for(int q = pEnd; q < qEnd; q <<= 1){
			for(int p = 1; p < pEnd; p <<= 1){
				answerSet.add( new BitVector(p + q) );
			}
		}
		return new Cell(answerSet, rank);
	}
	
	/**
	 * Returns the best Border Graph. What translation to do to the cell
	 * in order to get the best border graph has been previously figured out.
	 * @param rank, the number of ports involved
	 * @param cell, the cell which we are searching for a graph for
	 * @return the best Border Graph for cell
	 */
	private static Graph borderGraph(int rank, Cell cell){
		Cell borderEdges = borderEdges(rank, cell);
		return new Graph(rank, rank, borderEdges);
	}
	
	/**
	 * Translates this cell over all port assignments with it. Compares the
	 * border channels set and remembers the largest one out of all tried. This 
	 * is determined the best Border Graph. The BitVector used to translate 
	 * to the best Border Graph is returned
	 * @param rank, the number of ports being considered
	 * @param cell, the cell we are testing
	 * @return BitVector used to translate cell to best border graph
	 */
	private static BitVector bestBorderGraph(int rank, Cell cell){
		int xSize = -1;
		BitVector x = new BitVector(0);
		
		for(int i= 0; i < cell.size(); i++){
			BitVector y = cell.getPA()[i];
			Cell copy = new Cell(cell);
			copy.translate(y);
			Cell borderEdges = borderEdges(rank, copy);
			
			if( xSize < borderEdges.size() ){
				x = y;
				xSize = borderEdges.size();
			}
		}
		return x;
	}
	
	/**
	 * Returns a border graph. Not guaranteed to be the best (optimal) one.
	 * Returns A cell of BitVector Edges Consisting of edges from every port
	 * to every other port, as long as cell has it
	 * V = P
	 * E = every element of the power set of P with two elements (every possible
	 * 		doublet of ports)
	 * 
	 * @param rank, number of ports involved
	 * @param cell we are getting from
	 * @return set of border channel (bitVector) edges in a cell
	 */
	private static Cell borderEdges(int rank, Cell cell){
		Set<BitVector> answerSet = new HashSet<BitVector>();
		
		int limit = 1 << rank;
		for(int p = 1; p < limit; p = p << 1){
			for(int q = p << 1; q < limit; q = q << 1){
				BitVector ch = new BitVector(p + q);
				//nf = not finished
				boolean nf = true;
				//iterate through cell
				//try every port assignment as long as the last one worked
				for(int i = 0; nf && i < cell.size(); i++){
					BitVector current = cell.getPA()[i];
					nf = (  BitVector.intersection(ch, current).getNumber() > 0  ||
							cell.contains(BitVector.union(ch, current)) );
					
				}
				if(nf){
					answerSet.add(ch);
				}
			}
		}
		return new Cell(answerSet, rank);
	}
	
	public static void readTitle() throws Exception{
		String title = fileScanner.nextLine();
		String[] words = title.split(" ");
		String ranks = words[words.length - 1];
		ranks = ranks.substring(0, ranks.length() - 1);
		rank = Integer.parseInt(ranks);
	}
	
	public static Cell readCell() throws Exception{
		
		String cell  = fileScanner.nextLine();
		//skip over title
		while( ! cell.contains("K") ){
			cell = fileScanner.nextLine();
		}
		
		Scanner lineScanner = new Scanner(cell);
		name = lineScanner.next();
		
		String bitVectors = fileScanner.nextLine();
		
		lineScanner.close();
		lineScanner = new Scanner(bitVectors);
		
		Set<BitVector> allBVs = new HashSet<BitVector>();
		
		while(lineScanner.hasNext()){
			String bitvector = lineScanner.next();
			bitvector = bitvector.trim();
			int number = Integer.parseInt(bitvector);
			BitVector bV = new BitVector(number);
			allBVs.add(bV);
		}
		
		Cell input = new Cell(allBVs, rank);
		lineScanner.close();
		
		return input;
	}

}
