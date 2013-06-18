package makeGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import makeCell.GraphtoCell;
import makeCell.Histogram;
import shared.BitVector;
import shared.Cell;
import shared.Graph;
import shared.Permutations;

/**
 * This class reads a classification of all Kekule cells of a port number and attempts to find graphs for them.
 * 
 * @author Aaron
 *
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
		//I think this is how many internal verticies are allowed
		int internal = 4;
		ClassifyGraph.setAMG(internal);
		Histogram.rank = rank;
		Permutations.freePerm();
		
		while(input != null){
			
			Graph g = findGraph(rank, internal, input);
			g.setName(name);
			
			if(g != null){
				g.minimizeGraph();
				g.writeGraph();
				
				//try to find cell from graph
				//and make sure they match up
				//with compareL
			}
			
			try{
				input = readCell();
			} catch(Exception e){
				input = null;
			}
		}

	}
	
	private static Graph findGraph(int rank, int internal, Cell cell){
		//tries only the best border graph, and then decompositions
		Graph g1 = findGraphBEG(rank, internal, cell);
		
		if(g1 == null){
			System.out.println("Trying Decompositions");
			int i = 0;
			
			Cell p1 = null;
			Cell p2 = null;
			
			//i think this is not finished
			boolean nf = true;
			while(nf && i < cell.size()){
				BitVector pa = cell.getPA()[i];
				Cell other = new Cell(cell);
				other.translate(pa);
				if( ! other.indecomposable(p1, p2) ){
					g1 = findGraphBEG(rank, internal, p1);
					Graph g2 = findGraphBEG(rank, internal, p2);
					if(g1 != null && g2 != null){
						nf = false;
						g1 = Graph.oDot(g1, g2);
						g1.translate(pa);
					} else{
						g1 = null;
					}
					g2 = null;
				}
				i++;
			}
		}
		return g1;
	}
	
	private static Graph findGraphBEG(int rank, int internal, Cell cell){
		cell.sortBySize();
		//i think this is border edges
		BitVector x = bestBorderGraph(rank, cell);
		Cell nc = new Cell(cell);
		Graph g; 
		nc.translate(x);
		g = findGraphEG(rank, internal, nc);
		g.getEdgeCell().sortBySize();
		
		if(g != null){
			g.translate(x);
		}
		
		return g;
		
	}
	
	//cell is regular
	//therefore the subgraph of internal nodes has a matching
	//use classification of matched graphs of rank cN-cP
	private static Graph findGraphEG(int rank, int internal, Cell cell){
		Graph g0 = borderGraph(rank, cell);	
		Cell c0 = GraphtoCell.makeCell(g0);
		
		if( c0.size() == cell.size()){
			return g0;
		}
		if( internal < 2 ){
			return null;
		}
		
		Graph g1 = new Graph(rank, rank, null);
		Graph answer = null;
		while(answer == null && g1.getNumNodes() + 2 <= rank+internal ){
			
			g1.addTwoNodes();
			System.out.println("...trying " + g1.getNumNodes() + " nodes...");
			
			ArrayList<Cell> grs = ClassifyGraph.getAMG(g1.getNumNodes() - rank);
			Cell lEdges = potEdges(rank, g1.getNumNodes());
			for(int i = 0; i < grs.size(); i++){
				Cell gri = grs.get(i);
				//transfer edges from gri to g1
				Cell edgeSet =  g0.getEdgeCell();
				
				for(int j = 0; j < gri.size(); j++){
					int x = gri.getPA()[j].getNumber();
					edgeSet.add( new BitVector( x << rank ) );
				}
				g1.setEdgeCell(edgeSet);
				lEdges.sortBySize();
				
				answer = findGraphR(g1, c0, cell, lEdges, 0);
				
				if(answer != null){
					break;
				}
				else{
					g1.setEdgeCell(null);
				}
			}
		}
		
		
		return answer;
	}
	
	/**
	 * findGraph fo figure 1
	 * g0 = (v, E0) assume c0 sub cell, and c0 = Kp(g0)
	 * if possible return graph g with Kp = cell
	 * from g0 and edges from ledges[lptr...] else null
	 * preserves the arguments but modifes g0
	 */
	private static Graph findGraphR(Graph g0, Cell c0, Cell cell, Cell lEdges, int lptr){
		if( c0.size() == cell.size() ){
			return g0;
		}
		if( lptr == lEdges.size()){
			return null;
		}
		Cell edgeCell = new Cell(g0.getEdgeCell());
		Graph result = findGraphR(g0, c0, cell, lEdges, lptr + 1);
		if(result != null){
			return result;
		}
		
		//undo pushing of edges
		g0.setEdgeCell(edgeCell);
		BitVector edge = lEdges.getPA()[lptr];

		BitVector nodeV = g0.getNodeVector();
		BitVector uu = BitVector.symmetricDifference(nodeV, edge);
		
		Set<BitVector> kekCell = GraphtoCell.makeCell(uu, g0);
		Cell kk = new Cell(kekCell, g0.getNumPorts() );
		
		BitVector portV = g0.getPortVector();
		BitVector translation = BitVector.intersection(portV, edge);
		kk.translate(translation);
		
		if(kk.isSubSetOf(cell)){
			g0.addEdge(edge);
			Cell c1 = new Cell(c0);
			c1 = Cell.union(c1, kk);
			result = findGraphR(g0, c1, cell, lEdges, lptr + 1);
			return result;
		}
		return null;
	}
	
	private static Cell potEdges(int rank, int cn){
		Set<BitVector> answerSet = new HashSet<BitVector>();
		int size = rank * (cn - rank); 
		int pEnd = 1 << rank;
		int qEnd = 1 << cn;
		for(int q = pEnd; q < qEnd; q <<= 1){
			for(int p = 1; p < pEnd; p <<= 1){
				answerSet.add( new BitVector(p + q) );
			}
		}
		return new Cell(answerSet, rank);
	}
	
	private static Graph borderGraph(int rank, Cell cell){
		Cell borderEdges = borderEdges(rank, cell);
		return new Graph(rank, rank, borderEdges);
	}
	
	private static BitVector bestBorderGraph(int rank, Cell cell){
		int xSize = -1;
		BitVector x = new BitVector(0);
		
		for(int i= 0; i < cell.size(); i++){
			BitVector y =cell.getPA()[i];
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
	
	private static Cell borderEdges(int rank, Cell cell){
		Set<BitVector> answerSet = new HashSet<BitVector>();
		
		int limit = 1 << rank;
		for(int p = 1; p < limit; p = p << 1){
			for(int q = p << 1; q < limit; q = q << 1){
				BitVector ch = new BitVector(p + q);
				boolean nf = true;
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
	
	private static void readTitle() throws Exception{
		String title = fileScanner.nextLine();
		String[] words = title.split(" ");
		String ranks = words[words.length - 1];
		ranks = ranks.substring(0, ranks.length() - 1);
		rank = Integer.parseInt(ranks);
	}
	
	private static Cell readCell() throws Exception{
		
		String cell  = fileScanner.nextLine();
		//skip over title
		while( ! cell.contains("K") ){
			cell = fileScanner.nextLine();
		}
		
		Scanner lineScanner = new Scanner(cell);
		name = lineScanner.next();
		
		String bitVectors = fileScanner.nextLine();
		
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
