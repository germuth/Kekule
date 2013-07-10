package geneticAlgorithm;

import graphs.Graph;
import gui.GraphToSMILES;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import cdk.ImageRenderer;

import shared.BitVector;
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
	
	/**
	 * Outputs the best graphs of the population. Usually used at the end
	 * of the genetic algorithm. Currently top 20 graphs are printed minus
	 * duplicates of eachother in the top 20
	 */
	public void printTop3Edited(ArrayList<Cell> classy) {
		ArrayList<Graph> answer = new ArrayList<Graph>();
		for(int i = 0; i < 50; i++){
			answer.add( this.population.get(i) );
		}
		
		//cleaner output
		answer = Utils.deleteDuplicatesGraph(answer);
		
		for (int i = 0; i < answer.size(); i++) {
			Graph current = answer.get(i);
			for(int j = 0; j < current.getEdgeCell().size(); j++){
				BitVector edge = current.getEdgeCell().getPA()[j];
				
				Graph meow = new Graph(current);
				meow.removeEdge(edge);
				
				Cell original = GraphtoCell.makeCell( current );
				Cell remo = GraphtoCell.makeCell( meow );
				
				if( original.equals( remo ) ){
					//System.out.println("Edge Removed");
					current = meow;
					answer.set(i, meow);
				}
			}
		}
		
		answer = Utils.deleteDuplicatesGraph(answer);
		ArrayList<String> smiles = new ArrayList<String>();
		
		for (int i = 0; i < answer.size(); i++) {
			Graph current = answer.get(i);
			current.trimDisjoint();
			Cell c = GraphtoCell.makeCell( current );
			c.normalize();
			int index = 0;
			for(int j = 0; j < classy.size(); j++){
				if( c.equals(classy.get(j)) ){
					index = j + 1;
				}
			}
			Graph g = current.removeTriagles(c);
			if( g != null){
				current = g;
			}
			if( current.isDisjoint() ){
				g = current.connect(c);
				if( g != null){
					current = g;
				}
			}
			current.writeGraph();
			System.out.println( "K" + index + " " + c.toString() );
			if( !current.isDisjoint() ){
				String smile = GraphToSMILES.convertSMILES(current) ;
				smiles.add(smile);
				System.out.println(smile);
			}
			System.out.println("");
			
		}
		ImageRenderer.main(smiles);
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
		for(int i = 0; i < GeneticAlgorithm.ELITE_NUMBER; i++){
			nextGen.add(population.get(i));
		}
		//and 10 random ones
		for(int i = 0; i < GeneticAlgorithm.RANDOM_NUMBER; i++){
			nextGen.add( this.getRandom() );
		}
		
		return nextGen;
	}
	
	public int size(){
		return this.population.size();
	}
	
}
