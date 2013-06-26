package makeCell;

import graphs.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import shared.BitVector;
import shared.Cell;
import shared.Utils;

/**
 * Graph to Cell
 * 
 * This class reads in graphs from graphs.txt and attempts to find a Kekule cell
 * for each given graph. Graphs are then centered, and normalized according to
 * the classification found in Hesselink's paper.
 * 
 * Graphs are read in from graphs.txt in the following format:
 * 
 * Graph name #nodes #ports the set of ports edges extra edges (if necessary)
 * 
 * @author Aaron
 * 
 */
public class GraphtoCell {
	/**
	 * The file we are reading from. In this case "graphs.txt"
	 */
	private static File f;
	/**
	 * The scanner which reads from the file.
	 */
	private static Scanner s;

	private static Scanner fileScanner;

	private static Cell[] classification;
	
	/**
	 * Main method
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 *             , if file not found, program will exit
	 */
	/*
	public static void main(String[] args) throws FileNotFoundException {
		// f = new File("graphs.txt");
		// s = new Scanner(f);
		s = new Scanner(System.in);
		Graph inputGraph = null;

		prepareClass();

		try {
			// read in first graph from input
			inputGraph = readGraph();
		} catch (NoSuchElementException e) {
			System.out.println("file invalid");
			System.exit(0);
		}

		// while another graph can be read from input
		while (inputGraph != null) {
			// create Kekule cell for graph
			Set<BitVector> kCell = makeCell(inputGraph.getNodeVector(),
					inputGraph);
			Cell kekule = new Cell(kCell, inputGraph.getNumPorts());

			// briefly sort cell for output
			kekule.sortBySize();

			// print Kekule cell of graph before normalization
			String unweighted = "Unweighted " + kekule.printUnweighted();

			// normalize graph to fit classification by Hesselink
			kekule.normalize();

			// print graph name
			String graphName = inputGraph.getName() + ":";

			printClass(kekule, graphName, unweighted);
			System.out.println("");

			try {
				inputGraph = readGraph();
			} catch (NoSuchElementException e) {
				inputGraph = null;
			}
		}

	}
	*/

	public static Cell makeCell(Graph g) {
		return new Cell(makeCell(g.getNodeVector(), g), g.getNumPorts());
	}

	/**
	 * Makes a cell from a graph. Uses a bitVector bvNodes, which is the set of
	 * nodes we are currently considering 100101 would be 1st, 3rd, and 6th
	 * node. Uses recursion by removing components of the graph and finding the
	 * Kekule cell of smaller graphs.
	 * 
	 * This all follows the Section 4.1 of Hesselink's Paper
	 * 
	 * @param bvNodes
	 *            , set of nodes in BitVector form
	 * @param g
	 *            , graph we want a Kekule cell for
	 * @return Set of Bit Vectors representing the port assignments of the graph
	 */
	public static Set<BitVector> makeCell(BitVector bvNodes, Graph g) {

		Set<BitVector> kekuleCell = new HashSet<BitVector>();
		Set<BitVector> addend = new HashSet<BitVector>();

		BitVector ports = g.getPortVector();

		Set<BitVector> edges = Utils.arToSet(g.getEdgeCell().getPA());

		// Base case
		if (bvNodes.isEmpty()) {
			kekuleCell.add(new BitVector(0));
		} else {
			// grab first Vertex from nodes
			// does not remove it
			int nodeU = bvNodes.firstNode();
			// remove vertex from nodes
			bvNodes = bvNodes.remove(nodeU);

			if (ports.contains(nodeU)) {
				// place new bit vector to ensure different object
				kekuleCell = makeCell(new BitVector(bvNodes), g);
			} else {
				kekuleCell.clear();
			}

			// treat as ndh(u,g)
			// iterate over all edges
			Iterator<BitVector> i = edges.iterator();
			while (i.hasNext()) {
				BitVector edge = (BitVector) i.next();
				// if u in edge
				if (edge.contains(nodeU)) {

					// remove node U from edge
					BitVector newEdge = new BitVector(edge);
					newEdge = newEdge.remove(nodeU);
					// grab other node from edge
					int nodeV = newEdge.firstNode();

					// /if nodes has nodeV
					if (bvNodes.contains(nodeV)) {

						// remove v from nodes
						BitVector newNodes = new BitVector(bvNodes);
						newNodes = newNodes.remove(nodeV);

						// find sub cell
						addend = makeCell(newNodes, g);

						// create bit vector we will use as translation
						BitVector portAssignment = BitVector.intersection(
								ports, new BitVector(nodeU + nodeV));
						// translate over port assignment bit vector
						addend = Utils.translate(addend, portAssignment);

						// take union of answer and addend
						kekuleCell = Utils.union(kekuleCell, addend);

					}
				}
			}
		}

		return kekuleCell;
	}
	
	public static void printClass(Cell kekule, String gn, String un){
		for(int i = 0; i < GraphtoCell.classification.length; i++){
			if(kekule.equalsNoPorts(classification[i])){
				if(i + 1 >= 22){
					// print graph name
					System.out.println(gn);
					System.out.println(un);
					System.out.println(kekule.toString());
				}
				System.out.println("----- K" + (i + 1) + " -----");
			}
		}
	}
	

	public static void prepareClass() throws FileNotFoundException {
		File f = new File("myraw.txt");
		fileScanner = new Scanner(f);
		
		Cell[] classification = new Cell[24];
		try{
			Cell next = readCell();
			int i = 0;
			while(next != null){
				classification[i] = next;
				i++;
				next = readCell();
			}
		}catch(Exception e){
			
		}
		GraphtoCell.classification= classification;
	}

	public static Cell readCell() throws Exception {

		String cell = fileScanner.nextLine();
		// skip over title
		while (!cell.contains("K")) {
			cell = fileScanner.nextLine();
		}

		Scanner lineScanner = new Scanner(cell);
		String name = lineScanner.next();

		String bitVectors = fileScanner.nextLine();

		lineScanner.close();
		lineScanner = new Scanner(bitVectors);

		Set<BitVector> allBVs = new HashSet<BitVector>();

		while (lineScanner.hasNext()) {
			String bitvector = lineScanner.next();
			bitvector = bitvector.trim();
			int number = Integer.parseInt(bitvector);
			BitVector bV = new BitVector(number);
			allBVs.add(bV);
		}

		Cell input = new Cell(allBVs, 5);
		lineScanner.close();

		return input;
	}

}
