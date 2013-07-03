package all;

import graphs.ClassifyGraph;
import graphs.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import shared.Cell;
import shared.CellToGraph;
import shared.GraphtoCell;
import shared.Histogram;
import shared.InputParser;
import shared.Utils;
import classification.Classify;

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
			case 6: cheat();
			default:
				System.out.println("Number not Understood. Try Again");
			}
			
		}
	}
	
	private static void cheat(){
		File f = new File("FullClassificationRank6.txt");
		Scanner s = null;
		try {
			s = new Scanner(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int rank = 6;
		s.nextLine();
		s.nextLine();
		s.nextLine();
		
		ArrayList<Cell> classifications = new ArrayList<Cell>();
		
		Cell input = null;
		try {
			input = InputParser.readCell2(s);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(input != null){
			classifications.add(input);
			
			try{
				input = InputParser.readCell2(s);
			}
			catch(Exception e){
				input = null;
			}
		}
		
		System.out.println("Searching For Graphs: ");
		System.out.println("6 Internal Vertices are allowed at maximum by default.");
		int internal = 6;
		ClassifyGraph.setAMG(internal);
		Histogram.rank = rank;
		
		ArrayList<ArrayList<Graph>> graphsForEachCell = new ArrayList<ArrayList<Graph>>();
		
		for(int i = 0; i < classifications.size(); i++){
			System.out.println("K" + (i+1));
			Cell current = classifications.get(i);
			
			ArrayList<Graph> allGraphs = CellToGraph.findGraph(current.getNumPorts(), internal, current);
			CellToGraph.removeHighDegree(allGraphs);
			
			//remove disjoint graphs
			CellToGraph.removeDisjoint(allGraphs, current);
			
			//removes cycles of length 3 and maybe 4
			CellToGraph.widenCycles(allGraphs, current);
			
			allGraphs = Utils.removeNulls(allGraphs);
			
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
				
				if(currentCell.equalsNoPorts( classifications.get(j) )){
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
		
		for(int i = 0; i < classifications.size(); i++){
			System.out.println("K" + (i+1));
			Cell current = classifications.get(i);
			
			ArrayList<Graph> allGraphs = CellToGraph.findGraph(current.getNumPorts(), internal, current);
			allGraphs = Utils.deleteDuplicatesGraph(allGraphs);
			
			CellToGraph.removeHighDegree(allGraphs);
			
			//remove disjoint graphs
			CellToGraph.removeDisjoint(allGraphs, current);
			
			//removes cycles of length 3 and maybe 4 TODO: 4
			CellToGraph.widenCycles(allGraphs, current);
			
			allGraphs = Utils.removeNulls(allGraphs);
			
			graphsForEachCell.add(allGraphs);
		}
		System.out.println("Trying Template Molecules...");
		ArrayList<Graph> extras =  CellToGraph.tryTemplateMolecules( null , rank) ;
		for(int i = 0; i < extras.size(); i++){
			Graph current = extras.get(i);
			Cell currentCell = GraphtoCell.makeCell(current);
			currentCell.normalize();
			
			innerLoop:
			for(int j = 0; j < classifications.size(); j++){
				
				if(currentCell.equalsNoPorts( classifications.get(j) )){
					graphsForEachCell.get(j).add(current);
					break innerLoop;
				}
			}
		}
		//if add Edges is true
		//start from the beginning
		//and find first un found one
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
	
	//find a graph for cell
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
			
			//removes cycles of length 3 and maybe 4
			CellToGraph.widenCycles(allGraphs, cell);
			
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

	//finds a cell for a user inputted graph
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
	
	private static void printMenu(){
		System.out.println("Enter What You Would like to do:");
		System.out.println("1. Find a Cell for a Graph");
		System.out.println("2. Find a Graph for a Cell");
		System.out.println("3. Normalize a Cell");
		System.out.println("4. Classify and Find Stable Graphs for an Entire Rank");
		System.out.println("5. Quit");
	}

}
