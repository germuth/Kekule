package geneticAlgorithm;

import graphs.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
