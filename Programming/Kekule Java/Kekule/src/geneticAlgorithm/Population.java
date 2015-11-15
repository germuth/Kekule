package geneticAlgorithm;

import graphs.Graph;
import graphs.GraphToSMILES;
import gui.MutateMain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import shared.Cell;
import shared.GraphtoCell;
import shared.Utils;

/**
 * Population
 * 
 * A Population is list of graph used for the genetic algorithm. The list
 * is sorted by fitness so that the most fit graphs are at the begning of the list.
 * 
 * @author Aaron
 *
 */
public class Population {
	private ArrayList<Graph> population;
	private Random random;
	
	public Population(ArrayList<Graph> pop){
		Collections.sort(pop);
		this.population = pop;
		this.random = new Random();
	}
	
	public Graph getBest(){
		return this.population.get(0);
	}
	
	public Graph getRandom(){
		return this.population.get( this.random.nextInt(this.size()) );
	}

	/**
	 * Prints the current state of the population to Standard output.
	 * This includes the fitness of:
	 * The Best Graph
	 * Average Graph
	 * The Worst Graph
	 * 50th Graph ( top 10% in population of 500)
	 */
	public void printAverage(){
		int sum = 0;
		for(int i = 0; i < this.size(); i++){
			sum += this.population.get(i).getFitness();
		}
		System.out.println("Best: " + this.population.get(0).getFitness() );
		System.out.println("Average: " + (sum / this.size()));
		System.out.println("Worst: " + this.population.get( this.size()-1 ).getFitness() );
		System.out.println("50th " + this.population.get( 50).getFitness() );
	}
	
	public int getBestLength(int fitness){
		if( this.getBest().getFitness() != fitness ){
			return 0;
		} else{
			int count = 0;
			for(int i = 0; i < this.population.size(); i++){
				if( this.population.get(i).getFitness() == fitness){
					count++;
				} else{
					break;
				}
			}
			return count;
		}
	}
	
	/**
	 * Outputs the best graphs of the population. Usually used at the end
	 * of the genetic algorithm. Currently top 20 graphs are printed minus
	 * duplicates of each other in the top 20
	 */
	public ArrayList<String> getTopEdited(ArrayList<Cell> classy, int bestFitness) {
		ArrayList<Graph> answer = new ArrayList<Graph>();
		for(int i = 0; i < this.size(); i++){
			if( this.population.get(i).getFitness() == bestFitness){
				answer.add( this.population.get(i) );
			} else{
				break;
			}
		}
		
		ArrayList<String> smiles = new ArrayList<String>();
		for(Graph g: answer){
			Graph copy = new Graph(g);
			// test what cell this graph is
				Cell gsCell = GraphtoCell.makeCell(g);
				if (gsCell.size() != 0) {
					gsCell.normalize();
					gsCell.sortBySize();
					int index = classy.indexOf(gsCell);
					if(index != -1){
						g.tryToConnect();
						g.tryToFixCycleSize();	

 						for(int k = 0; k < 3; k++){
//							g = g.mergeNode();
							g.tryToFixCycleSize();
						}
						if (g.isRealistic()) {
							String smile = GraphToSMILES.convertSMILES(g);

							if (!smiles.contains(smile)) {
								g.writeGraph();
								System.out.println("K" + (index+1) + " " + gsCell.toString());
								smiles.add(smile);
//								System.out.println("SMILES: " + smile);
//								if (!g.isRealistic()) {
//									System.out.print("Baddy ");
//								}
								System.out.println(g.getFitness());
//								System.out.println("Cycles: " + g.getAllCycles());
								System.out.println("");

							}
						}else{
							System.out.println("GRAPH WAS BAD");
						}
					}else{
						System.out.println("SOMETHING WRONG WITH GENETIC ALGORITHM");
					}
				}
			}
//		
//		//cleaner output
//		answer = Utils.deleteDuplicates(answer);
//		ArrayList<String> smiles = new ArrayList<String>();
//		
//		for (int i = 0; i < answer.size(); i++) {
//			Graph current = answer.get(i);
//			
//			current.tryToFixCycleSize();
//			
//			if( current.isDisjoint() ){
//				if(current.tryToConnect()){
//					answer.add(current);
//				}
//			}
//			
//			Cell c = GraphtoCell.makeCell( current );
//			if (c.size() != 0) {
//				c.normalize();
//
//				int index = 0;
//				// identify which classification each graph is
//				for (int j = 0; j < classy.size(); j++) {
//					if (c.equals(classy.get(j))) {
//						index = j + 1;
//					}
//				}
//
//				if (!current.isDisjoint()) {
//					String smile = GraphToSMILES.convertSMILES(current);
//
//					if (!smiles.contains(smile)) {
//						current.writeGraph();
//						System.out.println("K" + index + " " + c.toString());
//						smiles.add(smile);
//						System.out.println("SMILES: " + smile);
//						if (!current.isRealistic()) {
//							System.out.print("Baddy ");
//						}
//						System.out.println(current.getFitness());
//						System.out.println("Cycles: " + current.getAllCycles());
//						System.out.println("");
//
//					}
//				} else {
//					System.err.println("THIS GRAPH WAS DISJOINT");
//				}
//			} else {
//				System.out.println("Graph had no cell?");
//			}
//		}
//		if( !smiles.isEmpty() ){
//		} else{
//			System.out.println("No Graphs Found!");
//			return new ArrayList<String>();
//		}
		return smiles;
	}
	
	/**
	 * Gets the next starting 100 graphs for the genetic
	 * algorithm. 
	 * 
	 * The list of graphs in a population is kept in sorted order. 
	 * This means the graphs with the highest fitness will be first. 
	 * Therefore we take the first 90 graphs in the population, and
	 * 10 random ones to ensure genetic diversity. 
	 * @return 100 graphs, compromising the survivors of last generation
	 */
	public ArrayList<Graph> getNextGeneration(){
		//for next generation the best 90 are picked
		ArrayList<Graph> nextGen = new ArrayList<Graph>();
		for(int i = 0; i < GAParameters.getEliteNumber(); i++){
			nextGen.add(population.get(i));
		}
		//and 10 random ones
		for(int i = 0; i < GAParameters.getRandomNumber(); i++){
			nextGen.add( this.getRandom() );
		}
		
		return nextGen;
	}
	
	public Graph get(int i ){
		return this.population.get(i);
	}
	public int size(){
		return this.population.size();
	}
	
}
