package all;

import graphs.ClassifyGraph;
import graphs.Graph;
import gui.MutateMain;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

import shared.BitVector;
import shared.Cell;
import shared.CellToGraph;
import shared.GraphtoCell;
import shared.Histogram;
import shared.InputParser;
import shared.Utils;
import classification.Classify;
/**
 * Main Class which includes all functionality as of June, 2013. 
 * 
 * Functionality includes
 * (1) Finding the normalized cell for a graph
 * 		- Taken right from Hesselink's C program
 * (2) Finding a graph for any normalized cell
 * 		- This is the main focus. In this case we aren't searching for any graphs, as Hesselink's did, 
 * we are searching for graphs which can be realized in stable molecules. This adds some restrictions
 * such as limited degree on nodes, a connected graph, cycles must have certain size and other 
 * minor constraints. Since the search space is much more restricted, and results even with these 
 * restrictions can't always be realized in stable molecules, instead of simply returning a single graph,
 * you are given a list of graphs. The first step is Hesselinks' approach, however, instead of the first
 * graph which satisfies the cell, it will return every graph it finds which satisfies the cell. Then we 
 * try using template molecules, and arrange the ports in all possible configurations, and see which
 * cells are created from that. Finally, we can try adding or removing edges from graphs we have 
 * already obtained in order to get graphs for something we haven't. 
 * (3) Classifies all cells of a give rank. directly from Hesselink
 * (4) Find graphs for all cells of a certain rank. This is done through the same steps as 2. 
 * 
 * @author Aaron
 *
 */
public class Main {
	private static Scanner input;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		input = new Scanner(System.in);
		int answer = 0;
		
		while (answer != 5) {
			printMenu();
			answer = input.nextInt();
			input.nextLine();
			switch (answer) {
			case 1:
				graphToCell();
				break;
			case 2: 
				cellToGraph(); 
				break;
			case 3: classify(); break;
			case 4: findGraphsRankWide(); break;
			case 5: System.exit(0);
			//sixth temporary option
			//searches for graphs with 6 ports, but loads the pre defined classification
			case 6: cheat(); break;
			case 7: findByRandom(); break;
			case 8: test(); break;
			default:
				System.out.println("Number not Understood. Try Again");
			}
			
		}
	}
	
	/**
	 * Attempts to find realistic graphs for all normalized cells of a given rank. The First 
	 * approach uses Hesselink's method. Subsequent approaches use a library of template molecules
	 * and try to add/remove edges to already obtained graphs. This takes a while. 
	 * First classifies all normalized cells of the given rank.
	 */
	private static void findGraphsRankWide(){
		long startTime = System.currentTimeMillis();
		System.out.println("Please enter the rank you want to search");
		int rank = input.nextInt();
		input.nextLine();
		
		boolean addEdges = false;
		System.out.println("Would you like to try adding edges to known graphs to search for unknown ones? (y/n)");
		String in = input.next();
		input.nextLine();
		if(in.contains("y")){
			addEdges = true;
		}
		
		
		System.out.println("Classifing all cells of rank " + rank);
		System.out.println("Raw Classifying...");
		//get raw classification
		//TODO naming: package.Class.method()
		ArrayList<Cell> classifications = Classify.classify(rank, 0);
	
		System.out.println("Cleaning Classification...");
		//trim raw classification to final result
		classifications = Classify.sortAndWeed(rank, classifications);
		System.out.println("Classification complete");
		
		System.out.println("Searching For Graphs: ");
		System.out.println("6 Internal Vertices are allowed at maximum by default.");
		int internal = 6;
		ClassifyGraph.setAMG(internal);
		Histogram.rank = rank;
		
		ArrayList<ArrayList<Graph>> graphsForEachCell = new ArrayList<ArrayList<Graph>>();
		
		//for each normalized cell try hesselink approach
		for(int i = 0; i < classifications.size(); i++){
			System.out.println("K" + (i+1));
			Cell current = classifications.get(i);
			//hesselink approach
			ArrayList<Graph> allGraphs = CellToGraph.findGraph(current.getNumPorts(), internal, current);
			allGraphs = Utils.deleteDuplicates(allGraphs);
	
			CellToGraph.removeHighDegree(allGraphs);
			
			//remove disjoint graphs
			CellToGraph.removeDisjoint(allGraphs, current);
			
			//removes cycles of length 3 and 4
			for(int k = 0; k < allGraphs.size(); k++){
				if(allGraphs.get(k) != null){
					allGraphs.get(k).widenCycles();					
				}
			}
			
			allGraphs = Utils.removeNulls(allGraphs);
			
			graphsForEachCell.add(allGraphs);
		}
		
		//Trying library of template molecules 
		//involves finding all possible port locations,
		//and trying every combination to see what cell
		//each one gives. 
		System.out.println("Trying Template Molecules...");
		ArrayList<Graph> extras =  CellToGraph.tryTemplateMolecules( null , rank) ;
		for(int i = 0; i < extras.size(); i++){
			Graph current = extras.get(i);
			Cell currentCell = GraphtoCell.makeCell(current);
			currentCell.normalize();
			
			innerLoop:
			for(int j = 0; j < classifications.size(); j++){
				
				if(currentCell.equals( classifications.get(j) )){
					graphsForEachCell.get(j).add(current);
					break innerLoop;
				}
			}
		}
		//if add Edges is true
		//start from the beginning
		//and find first not-found cell
		//search all below and try adding to get it
		if(addEdges){
			System.out.println("Trying to add edges...");
			for(int i = 0; i < graphsForEachCell.size(); i++){
				ArrayList<Graph> current = graphsForEachCell.get(i);
				//if no graphs found
				if( current.isEmpty() && i > 0){
					//try adding edges edges to all graphs below me
					//if they have graphs add here
					
					//for each known kekule cell below me
					for(int j = i-1; j >= 0; j--){
						System.out.println(j);
						if(graphsForEachCell.get(j).isEmpty()){
							continue;
						}
						
						ArrayList<Graph> below = graphsForEachCell.get(j);
						//adds edges to existing graphs to try and get new graph
						ArrayList<Graph> newOnes = CellToGraph.tryAdding(below, classifications.get(i) );
						
						//remove graphs with high degree on internal nodes
						//if graph has high degree on ports, try extending 
						//if fail, then remove
						CellToGraph.removeHighDegree(newOnes);
						
						//shouldn't be any disjoint edges if we've only added edges
						//remove disjoint graphs
						//CellToGraph.removeDisjoint(newOnes, classifications.get(i));
						
						//TODO ensure this can be commented out
						//removes cycles of length 3 and maybe 4
						//CellToGraph.widenCycles(newOnes, classifications.get(i));
						
						newOnes = Utils.removeNulls(newOnes);
						
						graphsForEachCell.get(i).addAll(newOnes);
					}
				}
			}
		}
		//23 has like 200 solutions can we trim some out???
		//go through solutions and grab the solution with the lowest amount of nodes
		for(int i = 0; i < graphsForEachCell.size(); i++){
			ArrayList<Graph> current = graphsForEachCell.get(i);
			int min = 999;
			int index = -1;
			for(int j = 0; j < current.size(); j++){
				Graph currentGraph = current.get(j);
				if( currentGraph.getNumNodes() < min ){
					min = currentGraph.getNumNodes();
					index = j;
				}
			}
			System.out.print("K" + (i+1) + " ");
			if (index != -1) {
				current.get(index).getEdgeCell().sortBySize();
				current.get(index).getEdgeCell().removeDuplicates();
				current.get(index).writeGraph();
			} else {
				System.out.println("No Graph Found!");
			}
		}
		long duration = System.currentTimeMillis() - startTime;
		System.out.println("Time Taken: " + (double)duration/1000.0 + " seconds.");
		
	}
	/**
	 * This method reads the FullClassifacitonRank6 txt file which already contains the 
	 * rank 6 classification. This is done to save time, as the classification takse many hours. This method 
	 * should be temporary and removed. The functionaly to skip steps however, should be moved in a proper
	 * sense.
	 */
	private static void cheat(){
		//reading classification
		File f = new File("FullClassificationRank6.txt");
		Scanner s = null;
		try {
			s = new Scanner(f);
		} catch (FileNotFoundException e) {
			System.err.println("File was unable to be found/read");
			e.printStackTrace();
		}
		int rank = 6;
		s.nextLine();
		s.nextLine();
		s.nextLine();
		
		ArrayList<Cell> classifications = new ArrayList<Cell>();
		
		Cell input = null;
		try {
			input = InputParser.readCell2(s, rank);
		} catch (Exception e1) {
			System.err.println("Cell was unable to be read from file");
			e1.printStackTrace();
		}
		
		while(input != null){
			classifications.add(input);
			
			try{
				input = InputParser.readCell2(s, rank);
			}
			catch(Exception e){
				input = null;
			}
		}
		
		// NOW STARTS the part where we try to find graphs for each cell
		//Takse multiple hours, in fact I've never let it finish yet
		//copy-pasted from option 4
		System.out.println("Searching For Graphs: ");
		System.out.println("6 Internal Vertices are allowed at maximum by default.");
		int internal = 6;
		ClassifyGraph.setAMG(internal);
		Histogram.rank = rank;
		
		ArrayList<ArrayList<Graph>> graphsForEachCell = new ArrayList<ArrayList<Graph>>();
		
		for(int i = 0; i < classifications.size(); i++){
			System.out.println("K" + (i+1));
			Cell current = classifications.get(i);
			
			ArrayList<Graph> allGraphs = new ArrayList<Graph>();
//			ArrayList<Graph> allGraphs = CellToGraph.findGraph(current.getNumPorts(), internal, current);
//			CellToGraph.removeHighDegree(allGraphs);
//			
//			//remove disjoint graphs
//			CellToGraph.removeDisjoint(allGraphs, current);
//			
//			//removes cycles of length 3 and 4
//			for(int k = 0; k < allGraphs.size(); k++){
//				if(allGraphs.get(k) != null){
//					allGraphs.get(k).widenCycles();					
//				}
//			}
//			
//			allGraphs = Utils.removeNulls(allGraphs);
			
			graphsForEachCell.add(allGraphs);
		}
		
		System.out.println("Trying Template Molecules");
		ArrayList<Graph> extras =  CellToGraph.tryTemplateMolecules( null , rank) ;
		for(int i = 0; i < extras.size(); i++){
			Graph current = extras.get(i);
			Cell currentCell = GraphtoCell.makeCell(current);
			currentCell.normalize();
			
			innerLoop:
				for(int j = 0; j < classifications.size(); j++){
					
					if(currentCell.equals( classifications.get(j) )){
						graphsForEachCell.get(j).add(current);
						break innerLoop;
					}
				}
		}
		System.out.println("Done");
		
		for(int i = 0; i < graphsForEachCell.size(); i++){
			ArrayList<Graph> current = graphsForEachCell.get(i);
			int min = 999;
			int index = -1;
			for(int j = 0; j < current.size(); j++){
				Graph currentGraph = current.get(j);
				if( currentGraph.getNumNodes() < min ){
					min = currentGraph.getNumNodes();
					index = j;
				}
			}
			System.out.print("K" + (i+1) + " ");
			if (index != -1) {
				current.get(index).getEdgeCell().sortBySize();
				current.get(index).getEdgeCell().removeDuplicates();
				current.get(index).writeGraph();
			} else {
				System.out.println("No Graph Found!");
			}
		}
	}
	
	/**
	 * Takes in a cell from input and finds its classification. Basically, normalizes
	 * a cell for you. Uses hesselinke method.
	 */
	private static void classify(){
		Cell cell = InputParser.readCell(input);
		Histogram.rank = cell.getNumPorts();

		while(cell != null){
			
			System.out.println("Unweighted " + cell.printUnweighted() );
			cell.normalize();
			System.out.println(cell.toString());
			
			try{
				cell = InputParser.readCell(input);
			} catch(Exception e){
				cell = null;
			}
		}
	}
	
	/**
	 * Represents option 2
	 * 
	 * Attempts to find a graph for a cell. first uses hesselinks method, then tries
	 * using a library of template molecules
	 * 
	 * Graphs must have a degree belowe 3, and ports below 2, must be connected, all
	 * cycles must be of length at least 4( maybe 5)
	 */
	private static void cellToGraph(){
		System.out.println("6 Internal Vertices are allowed at maximum by default.");
		int internal = 6;
		ClassifyGraph.setAMG(internal);
		
		//TODO: what if cell isn't normalized must check this
		Cell cell = InputParser.readCell(input);
		Histogram.rank = cell.getNumPorts();
		//Permutations.freePerm();
		
		while(cell != null){
			//get hesselink's graphs
			ArrayList<Graph> allGraphs = CellToGraph.findGraph(cell.getNumPorts(), internal, cell);
			CellToGraph.removeHighDegree(allGraphs);
			
			//remove disjoint graphs
			CellToGraph.removeDisjoint(allGraphs, cell);
			
			//removes cycles of length 3 and 4
			for(int k = 0; k < allGraphs.size(); k++){
				allGraphs.get(k).widenCycles();
			}
			
			allGraphs.addAll( CellToGraph.tryTemplateMolecules( cell, cell.getNumPorts()) );
			
			allGraphs = Utils.removeNulls(allGraphs);
			
			int min = 999;
			int index = -1;
			for(int i = 0; i < allGraphs.size(); i++){
				Graph current = allGraphs.get(i);
				if( current.getNumNodes() < min ){
					min = current.getNumNodes();
					index = i;
				}
			}
			if(index == -1){
				System.out.println("No Graph Found!");
			}
			else {
				if (allGraphs.get(index).isDisjoint()) {
					System.out.println("DisJoint");
				}
				allGraphs.get(index).getEdgeCell().sortBySize();
				allGraphs.get(index).getEdgeCell().removeDuplicates();
				allGraphs.get(index).writeGraph();
			}
			try{
				cell = InputParser.readCell(input);
			} catch(Exception e){
				cell = null;
			}
		}
	}

	/**
	 * Uses Hesselink's method to find a Cell for a given graph.
	 */
	private static void graphToCell() {
		InputParser.askForGraph();
		Graph g = InputParser.readGraph(input);

		while (g != null) {
			Cell cell = GraphtoCell.makeCell(g);

			System.out.println(g.getName() + ":");

			// print Kekule cell of graph before normalization
			System.out.println("Un-Normalized " + cell.printUnweighted());

			// normalize graph to fit classification by Hesselink
			cell.normalize();

			System.out.println(cell.toString());
			System.out.println("");
			
			g = InputParser.readGraph(input);
		}
		
		System.out.println("Graphs Complete");
	}
	
	/**
	 * Prints the Menu Dispay for the user
	 */
	private static void printMenu(){
		System.out.println("Enter What You Would like to do:");
		System.out.println("1. Find a Cell for a Graph");
		System.out.println("2. Find a Graph for a Cell");
		System.out.println("3. Normalize a Cell");
		System.out.println("4. Classify and Find Stable Graphs for an Entire Rank");
		System.out.println("5. Quit");
	}

	private static void findByRandom(){
		//reading classification
		File f = new File("FullClassificationRank6.txt");
		Scanner s = null;
		try {
			s = new Scanner(f);
		} catch (FileNotFoundException e) {
			System.err.println("File was unable to be found/read");
			e.printStackTrace();
		}
		int rank = 6;
		s.nextLine();
		s.nextLine();
		s.nextLine();
		
		ArrayList<Cell> classifications = new ArrayList<Cell>();
		
		Cell input = null;
		try {
			input = InputParser.readCell2(s, rank);
		} catch (Exception e1) {
			System.err.println("Cell was unable to be read from file");
			e1.printStackTrace();
		}
		
		while(input != null){
			classifications.add(input);
			
			try{
				input = InputParser.readCell2(s, rank);
			}
			catch(Exception e){
				input = null;
			}
		}
		
		
		System.out.println("Searching For Graphs: ");

		ArrayList<ArrayList<Graph>> graphsForEachCell = new ArrayList<ArrayList<Graph>>();
		
		graphsForEachCell = findGraphsRandomly(classifications);
//		for(int i = 0; i < classifications.size(); i++){
//			System.out.println("K" + (i+1));
//			Cell current = classifications.get(i);
//			
//			graphsForEachCell.add(findGraphRandomly(current));
//		}
		System.out.println("Done");
		
		ArrayList<Graph> theGraphs = new ArrayList<Graph>();
		for(int i = 0; i < graphsForEachCell.size(); i++){
			ArrayList<Graph> current = graphsForEachCell.get(i);
			Collections.sort(current, new Comparator<Graph>(){

				@Override
				public int compare(Graph o1, Graph o2) {
					return new Integer(o1.getNumNodes()).compareTo(o2.getNumNodes());
				}
				
			});
			System.out.print("K" + (i+1) + " ");
			if(!current.isEmpty()){
//				for(int j = 0; j < current.size(); j++){
					Graph currentG = current.get(0);
					currentG.widenCycles();
					currentG.getEdgeCell().sortBySize();
					currentG.getEdgeCell().removeDuplicates();
					currentG.writeGraph();
					
					theGraphs.add(currentG);
//				}
			} else {
				System.out.println("No Graph Found!");
			}
		}
		
		MutateMain.showGraphs(theGraphs);
	}
	
	private static ArrayList<ArrayList<Graph>>findGraphsRandomly(ArrayList<Cell> classifications) {
		ArrayList<ArrayList<Graph>> graphs = new ArrayList<ArrayList<Graph>>();
		for(int i = 0; i < 214; i++){
			graphs.add(new ArrayList<Graph>());
		}
		Random random = new Random();
		
		//only try 1000 times
		for(int i = 0; i < 10000; i++){
			if(i % 1000 == 0){
				System.out.println(i);
			}
			int nP = 6;
			int nC = 6;

			// add anywhere from (to) -> (from) nodes
			int from = 0;
			int to = 20;
			nC += from + random.nextInt(to - from);

			// edges always added
			// add atleast enough to connect all your nodes
			// which is num nodes - 1
			// PLUS from -> to
			from = 0;
			to = 25;
			int edgesToAdd = nC - 1 + from + random.nextInt(to - from);

			Cell c = new Cell();
			c.setNumPorts(6);

			// the graph
			Graph newbie = new Graph("G" + (i+1), nP, nC, c);

			// loop adding all the edges
			// care must be taken that
			// we don't add an edge we already have
			// the bit Vector generated is a valid edge
			// the edge doesn't overflow the max degree allocated
			innerloop : for (int j = 0; j < edgesToAdd; j++) {
				int node1 = 1 << random.nextInt(nC);
				int node2 = 1 << random.nextInt(nC);

				BitVector bv = new BitVector(node1 + node2);

				int attempts = 0;
				while (newbie.isBadEdge(bv) && attempts < 15) {
					node1 = 1 << random.nextInt(nC);
					node2 = 1 << random.nextInt(nC);
					bv = new BitVector(node1 + node2);
					attempts++;
				}

				if (attempts < 15) {
					newbie.addEdge(bv);
				} else {
					break innerloop;
				}
			}

			Cell gsCell = GraphtoCell.makeCell(newbie);
			// cell may be empty if there is a secluded port
			if (gsCell.size() == 0 || newbie.hasBadCycles()) {
				continue;
			} else {
				gsCell.normalize();
				gsCell.sortBySize();
				int index = classifications.indexOf(gsCell);
				if(index == -1){
					continue;
				}else{
					graphs.get(index).add(newbie);
				}
			}
		}
		return graphs;
	}
	
	public static void test(){
//		K174 G174_P: 6 Nodes,  6 Ports
//		Edges: 0-1, 0-2, 1-3, 2-3, 2-4, 3-4, 0-5, 1-5, 4-5
		Graph g = InputParser.readGraph(input);
		if(g.hasBadCycles()){
			System.out.println("bad cycles");
		}else{
			System.out.println("nope, they are good");
		}
	}
}
