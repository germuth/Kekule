package shared;

import graphs.Graph;
import graphs.TemplateMolecule;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


public class InputParser {

	public static Cell readCell(Scanner input){

		int rank = Integer.parseInt( input.nextLine() );
		String cell = input.nextLine();

		Scanner lineScanner = new Scanner(cell);
		String name = lineScanner.next();

		String bitVectors = input.nextLine();

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

		
		Cell in = new Cell(allBVs, rank);
		lineScanner.close();

		return in;
	}

	public static void askForGraph() {
		System.out.println("Please Input Graphs with Following Format:");
		System.out.println("Name");
		System.out.println("#Nodes #Ports");
		System.out.println("Ports");
		System.out.println("Edges");
		System.out.println("Extra Edges");
		System.out.println("There must an empty line between graphs");
	}

	/**
	 * Reads a single graph from graphs.txt and returns it as Graph object.
	 * Graphs are kept in the following format
	 * 
	 * Name #Nodes #Ports Ports Edges Extra Edges
	 * 
	 * For example:
	 * 
	 * aMoleculeName 7 3 0 1 2 0-1-2-3-4-6 4-5, 5-6
	 * 
	 * @return Graph object read from text file
	 * @throws FileNotFoundException
	 *             , if file not found
	 */
	public static Graph readGraph(Scanner input) {

		String name = input.nextLine();
		if (name.isEmpty()) {
			return null;
		}
		int numNodes = input.nextInt();
		int numPorts = input.nextInt();

		input.nextLine();

		String ports = input.nextLine();

		int[] portRemapping = getPortPermutation(numNodes, numPorts, ports);

		String inputEdges = input.nextLine();
		String inputExtraEdges = input.nextLine();

		// parses close edge format "0-1-2-3"
		Set<String> edges = parseForEdgesCompact(inputEdges, portRemapping);

		if (!inputExtraEdges.isEmpty()) {
			// parses extra edge format "0-1, 1-2"
			Set<String> extraEdges = parseForEdges(inputExtraEdges,
					portRemapping);
			edges.addAll(extraEdges);
			input.nextLine();
		}

		return new Graph(name, numPorts, numNodes, edges);
	}
	//Benzene
	//6 nodes 8 possiblePorts
	//edges
	//extra edges
	public static TemplateMolecule readTemplateMolecule(Scanner input, int numPorts) {

		String name = input.nextLine();
		if (name.isEmpty()) {
			return null;
		}
		int numNodes = input.nextInt();
		int possiblePorts = input.nextInt();
		input.nextLine();

		String inputEdges = input.nextLine();
		String inputExtraEdges = input.nextLine();

		return new TemplateMolecule(name, numNodes, possiblePorts, inputEdges, inputExtraEdges);
	}

	/**
	 * Parses long string of form 0-1-2-3-4-5-6-7-8-9 and returns string array
	 * of format {0 1, 1 2, 2 3, 3 4, 4 5, etc}
	 * 
	 * @param inputEdges
	 * @return
	 */
	public static Set<String> parseForEdgesCompact(String inputEdges,
			int[] remapping) {
		Set<String> edges = new HashSet<String>();

		// should get
		// { "1", "2", "3", "4" } etc
		// numbers are at every even index of array
		String[] edgeArray = inputEdges.split("-");

		int index = 0;
		String first = edgeArray[index];
		index++;
		first = first.trim();
		String second = edgeArray[index];
		index++;
		second = second.trim();

		// convert first and second to ints
		int fir = Integer.parseInt(first);
		int sec = Integer.parseInt(second);

		// use remapping
		if(remapping != null){
			edges.add(remapping[fir] + " " + remapping[sec]);
		}
		else{
			edges.add(first + " " + second);
		}
		while (index < edgeArray.length) {
			first = second;
			second = edgeArray[index];
			second = second.trim();
			index++;

			// convert first and second to ints
			fir = Integer.parseInt(first);
			sec = Integer.parseInt(second);

			// use remapping
			if(remapping != null){
				edges.add(remapping[fir] + " " + remapping[sec]);
			}
			else{
				edges.add(first + " " + second);
			}
		}

		return edges;
	}

	/**
	 * Parses long string of form 0-1, 5-6, 8-9 and returns string array of
	 * format {0 1, 1 2, 2 3, 3 4, 4 5, etc}
	 * 
	 * @param inputEdges
	 * @return
	 */
	public static Set<String> parseForEdges(String inputEdges, int[] remapping) {
		Set<String> extraEdges = new HashSet<String>();

		// should get
		// { "0-1", "5-6", "8-9"} etc
		// ranges are at every even index of array
		String[] edgeArray = inputEdges.split(",");

		int index = 0;
		while (index < edgeArray.length) {
			// grab every second element
			String edge = edgeArray[index];
			index++;
			// use other method to parse 0-1, and return "0 1"
			Set<String> singleEdge = parseForEdgesCompact(edge, remapping);
			// add to set of extra edges
			extraEdges.addAll(singleEdge);
		}

		return extraEdges;
	}

	/**
	 * Remaps the nodes so the ports are the first 0 - numPorts nodes. This
	 * allows the edge numbers to be changed based off of the new node
	 * permutation. This is what makes the difference between what nodes your
	 * ports are on. In stead of moving the algorithm to our ports, we move
	 * every other node around so the ports are first.
	 * 
	 * @param nodeNum
	 *            , the number of nodes
	 * @param portNum
	 *            , the number of ports
	 * @param ports
	 *            , a string of ports
	 * @return port remapping array
	 */
	public static int[] getPortPermutation(int nodeNum, int portNum,
			String ports) {
		int[] remapping = new int[nodeNum];

		// fill with below zero
		for (int i = 0; i < remapping.length; i++) {
			remapping[i] = -1;
		}

		Scanner s = new Scanner(ports);
		// keep track of current node
		int currentNode = 0;

		while (s.hasNext()) {
			String num = s.next();
			int number = Integer.parseInt(num);
			remapping[number] = currentNode++;
		}

		for (int i = 0; i < remapping.length; i++) {
			if (remapping[i] < 0) {
				remapping[i] = currentNode++;
			}
		}
		s.close();

		return remapping;
	}
}
