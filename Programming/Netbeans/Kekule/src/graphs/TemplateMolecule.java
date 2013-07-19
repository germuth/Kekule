package graphs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import shared.InputParser;
import shared.PowerSet;

/**
 * Template Molecule
 * 
 * This class represents a template Molecule. Template Molecules are molecules read in from a library (text file). 
 * Based off the information there, a graph of the molecule is given along with every possible node that could be
 * a port. We can then try every possible combination and see the resulting graph and therefore cell. 
 * 
 * This is a useful technique for finding realistic graphs for many cells.
 * @author Aaron
 *
 */
public class TemplateMolecule {
	
	//name of molecule
	private String name;
	
	//number of nodes
	private int numNodes;
	
	//the number of nodes which can have a port
	//those nodes are numbered 1 - i
	private int possiblePorts;
	
	//String of edges 1-2-3-4
	private String edges;
	
	//extra edges 1-2,2-3
	private String extraEdges;
	
	//constructor
	public TemplateMolecule(String name, int nN, int possiblePorts, String edges, String extraEdges){
		this.name = name;
		this.numNodes = nN;
		this.possiblePorts = possiblePorts;
		this.edges = edges;
		this.extraEdges = extraEdges;
	}

	//must give number of ports you are looking for 
	//in getALlGraphs()
	public ArrayList<Graph> getAllGraphs(int rank){
		ArrayList<Graph> myGraphs = new ArrayList<Graph>();

		//set of all possible ports
		Set<Integer> allPossiblePorts = new HashSet<Integer>();
		for(int i = 1; i <= possiblePorts; i++){
			allPossiblePorts.add(i);
		}
		
		PowerSet<Integer> powerSet = new PowerSet<Integer>(allPossiblePorts, rank, rank);
		
		Iterator<Set<Integer>> portGroups = powerSet.iterator();
		//graph number
		int i = 0;
		while(portGroups.hasNext()){
			Set<Integer> portGroup = portGroups.next();
			
			if(portGroup.isEmpty()){
				continue;
			}
			//convert set to string
			String ports = "";
			for(Integer a: portGroup){
				ports += a + " ";
			}
			
			int[] portRemapping = InputParser.getPortPermutation(numNodes, rank, ports);

			// parses close edge format "0-1-2-3"
			Set<String> edges = InputParser.parseForEdgesCompact(this.edges, portRemapping);
			
			if (!this.extraEdges.isEmpty()) {
				// parses extra edge format "0-1, 1-2"
				Set<String> extraEdges = InputParser.parseForEdges(this.extraEdges,
						portRemapping);
				edges.addAll(extraEdges);
			}
			Graph current = new Graph(this.name + i, rank, this.numNodes, edges);
			i++;
			myGraphs.add(current);
		}

		return myGraphs;
	}
}
