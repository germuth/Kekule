package all;

import graphs.ClassifyGraph;
import graphs.Graph;

import java.util.ArrayList;
import java.util.Scanner;

import makeCell.GraphtoCell;
import makeGraph.CellToGraph;
import shared.Cell;
import shared.Histogram;
import shared.InputParser;
import shared.Permutations;
import shared.Utils;

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
			// case 3: classify(); break;
			// case 4: complementation(); break;
			default:
				System.out.println("Number not UnderStood. Try Again");
			}
			
		}
	}
	
	//find a graph for cell
	private static void cellToGraph(){
		System.out.println("6 Internal Vertices are allowed at maximum by default.");
		int internal = 6;
		ClassifyGraph.setAMG(internal);
		
		Cell cell = InputParser.readCell(input);
		Histogram.rank = cell.getNumPorts();
		Permutations.freePerm();
		
		while(cell != null){
			//get hesselink's graphs
			ArrayList<Graph> allGraphs = CellToGraph.findGraph(cell.getNumPorts(), internal, cell);
			CellToGraph.removeHighDegree(allGraphs);
			
			allGraphs.addAll( CellToGraph.tryTemplateMolecules(cell) );
			
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
			
			allGraphs.get(index).writeGraph();
			
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
		System.out.println("3. Classify a Cell");
		System.out.println("4. Classify and Find Stable Graphs for an Entire Rank");
		System.out.println("5. Quit");
	}

}
