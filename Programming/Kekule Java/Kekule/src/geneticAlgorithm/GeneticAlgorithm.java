package geneticAlgorithm;

import graphs.Graph;

import java.util.ArrayList;
import java.util.Random;

import newGui.LoadingBar;
import shared.BitVector;
import shared.Cell;
import shared.GraphtoCell;
import shared.InputParser;
/**
 * This class uses a genetic algorithm to attempt to find realistic graphs for a given cell. 
 * Realistic graphs have limited degree, must be connected, and have cycles greater than 4.
 * Some graphs obtained even with these restrictions will not be realistic. 
 * 
 * Creates a random population of graphs. Out of each population, 100 are chosen and then mutated, 
 * and crossover-ed to create the other 400, filling the population. This process is repeated and 
 * then the best couple graphs are displayed to the user.
 * 
 * Numbers above are default and the actual values used are the static members variables of the class below.
 * 
 * @author Aaron
 *
 */
public class GeneticAlgorithm{
	/**
	 * The Amount of Graphs which are in the population each Iteration of the 
	 * Genetic Algorithm. Higher is normally more accurate, however, takes
	 * longer
	 */
	private static final int POPULATION_SIZE = 1000;
	/**
	 * The Amount of iterations the genetic algorithm will perform until stopping.
	 * More Iterations move the entire population closer to the optimal result, but
	 * again takes longer.
	 */
	private static final int ITERATIONS = 100;
	/**
	 * The amount of graphs which are taken from the previous population
	 * to the next population based only on health. Basically, this means
	 * the top 90 of each generation are passed to the next generation. 
	 */
	public static final int ELITE_NUMBER = 140;
	/**
	 * The amount of graphs which are taken from the previous population
	 * randomly to the next population. In this case, 90 are taken from
	 * the best of last generation, and 10 are randomly taken for genetic
	 * diversity.
	 */
	public static final int RANDOM_NUMBER = 60;
	/**
	 * The amount of graphs generated from a population by simple mutation. 
	 * In our case, 100 graphs are initially taken in, and these graphs 
	 * are mutated to give 200 additional graphs.
	 */
	private static final int MUTANT_NUMBER = 400;
	/**
	 * The amount of graphs generated from crossover. In our case, out of the 100
	 * starting elites, two are chosen at random 200 to crossover and create a new
	 * graph. The start elite + mutantNumber + crossOverNumber = 500, the 
	 * entire population size.
	 */
	private static final int CROSSOVER_NUMBER = 400; 
	/**
	 * The amount of graphs with maximum fitness that are required before the
	 * genetic algorithm will terminate. If this number is never reached, it
	 * will terminate when the number of iterations is reached
	 */
	private static final int MINIMUM_GRAPHS_REQUIRED = 3;
	/**
	 * Random Number Generator used for all randomness of the genetic algorithm.
	 */
	private static final Random random = new Random();
	
	/**
	 * The amount of ports in the cell, that which we are trying to find a graph
	 * for
	 */
	private static int rank;
	/**
	 * The cell, coming from user input, that which we are tring to find a graph 
	 * for
	 */
	private static Cell cell;
	/**
	 * List of Cell classifications of given rank. Used to tell what classification
	 * number inputed cells are
	 * TODO generate text files for rank 1 2 3
	 */
	private static ArrayList<Cell> classifications;
	
	private static GeneticAlgorithmTask gat;
	
	/**
	 * Sets up the genetic algorithm, and then calls the
	 * run() method of this class to actually perform
	 * the genetic algorithm.
	 * @param Cell c, the cell we are evolving towards
	 * @param MainWindow aj, the graphical interface
	 */
	public static ArrayList<String> setUpAndRun(Cell c, GeneticAlgorithmTask gat){
		
		GeneticAlgorithm.gat = gat;
		//Take in cell from user
		//Scanner input = new Scanner(System.in);
		cell = c;
		
		//cell = InputParser.readCell(input);
		cell.normalize();
		
		int maxFitness = cell.size();
		
		rank = cell.getNumPorts();
		
		classifications = InputParser.readClassification(rank);
		
		gat.setProgressBar(71);
		//lb.setStage(1);
		
		//Generate the initial population randomly
		System.out.println("Generating Population");
		Population population = generateInitialPopulation();
		
		//lb.setStage(2);
		
		long startTime = System.currentTimeMillis();
		
		gat.setProgressBar(0);
		
		geneticAlgorithm(population, maxFitness);
		
		population.printAverage();
		if( population.getBestLength( maxFitness) < 1){
			System.out.println("No Graphs Found.");
		}
		
		long duration = System.currentTimeMillis() - startTime;
		System.out.println("Time Taken: " + (double)duration/1000.0 + " seconds.");
		
		return population.getTopEdited(classifications, maxFitness);
	}
	
	/**
	 * Runs the genetic algorithm. A population of graphs is evolved towards the required
	 * cell. Numbers in comments below are simply default values, and the actual values currently
	 * used are the static variables of this class. 
	 * TODO However, once the graphical interface is working properly, all values will come from there.
	 */
	private static void geneticAlgorithm(Population population, int maxFitness){
		
		ArrayList<Graph> nextGen = null;
		for(int i = 0; i < ITERATIONS; i++){
			//if we've found enough graphs, quit
			if( population.getBestLength(maxFitness) >= GeneticAlgorithm.MINIMUM_GRAPHS_REQUIRED){
				break;
			}
			
			gat.setProgressBar( (int) (((double)(population.getBestLength(maxFitness)) 
					/ (double)GeneticAlgorithm.MINIMUM_GRAPHS_REQUIRED) * 100) );
			
			//print out progress report every 10 percent
			double progress = (double)i/(double)ITERATIONS;
			if((progress*100) % 5 == 0){
				System.out.println(progress);
			}
			
			//grab 100 graphs for the next iteration
			//90 are elite
			//10 are random for genetic diversity
			nextGen = population.getNextGeneration();
			
			//mutate 200 random Graphs and add to nextGen
			//since we're picking from 100 elites
			//likely to get each graph twice
			//must add mutants to separate list to ensure they are not crossover-ed this iteration
			ArrayList<Graph> mutants = new ArrayList<Graph>();
			for(int j = 0; j < MUTANT_NUMBER; j++){
				Graph mutant = nextGen.get( random.nextInt( nextGen.size()) );
				mutant.setFitness( 0 );
				mutants.add( mutateGraph(mutant) );
			}
			
			//Perform crossover
			//two random graphs from elite are chosen and combined
			for(int j = 0; j < CROSSOVER_NUMBER; j++){
				//TODO use 4 parents
				//get two random parents
				Graph parent1 = nextGen.get( random.nextInt( nextGen.size()) );
				Graph parent2 = nextGen.get( random.nextInt( nextGen.size()) );
				
				Graph child = crossover(parent1, parent2);
				child.setFitness( 0 );
				nextGen.add(child);
			}
			
			nextGen.addAll(mutants);
			
			// fitness function on every graph
			for (int j = 0; j < nextGen.size(); j++) {
				Graph current = nextGen.get(j);
				calculateFitness(current);
			}

			gat.setProgressBar(100);
			
			population = new Population( nextGen );
		}
	}

	/**
	 * Calculates the fitness of a given Graph g. Fitness is calculated as follows.
	 * The cell of Graph g is compared to the Cell we are looking for. For Every Port assignment
	 * that both share, the fitness value is increased by one. However, for every port assignment
	 * which g has but the cell doesn't, or a port assignment which is missing from g but present
	 * in the answer cell, the fitness is reduced one point. If the cell is empty ( a secluded port
	 * was made), the cell is automatically assigned a fitness of -10, so it is not used in future 
	 * generations.
	 * 
	 * Assigns the fitness value calculated to the graphs 'fitness' field.
	 * 
	 * @param g, the graph we are calculating the fitness for
	 */
	public static void calculateFitness( Graph g){
		//fitness already calculated
		if( g.getFitness() != 0){
			return;
		}
		
		Cell answer = cell;

		Cell gsCell = GraphtoCell.makeCell(g);
		// cell may be empty if there is a secluded port
		if (gsCell.size() == 0) {
			g.setFitness(-10);
		} 
		else {
			gsCell.normalize();
			gsCell.sortBySize();

			int answerIndex = 0;
			int gsIndex = 0;

			double fitness = 0;

			while (answerIndex != answer.size() && gsIndex != gsCell.size()) {
				BitVector correct = answer.getPA()[answerIndex];
				BitVector gBV = gsCell.getPA()[gsIndex];

				// if it has the port assignment
				if (correct.equals(gBV)) {
					fitness++;

					answerIndex++;
					gsIndex++;
				} else {
					fitness--;
					// if it doesn't have the port assignment
					if (correct.getNumber() < gBV.getNumber()) {
						answerIndex++;
					}
					// if it has extra port assignment
					else {
						gsIndex++;
					}
				}

			}

			// if answer or g have extra port assignments which were not reached
			// we must subtract 1 fitness for each of those
			// only one of these terms should ever be non-zero
			fitness -= ( ( answer.size() - answerIndex )
					+ ( gsCell.size() - gsIndex ) );
			
			//if graph has two cycles which share more than one edge, this is infeasible
			//in carbon chemistry
			if( g.hasBadCycles() ){
				fitness -= 0.5;
			}
			g.setFitness(fitness);

		}
	}
	
	/**
	 * Performs crossover on two random graphs. Crossover is perform by iterating through
	 * the edges of both graphs. If both graphs share an edge, the child gets that edge. 
	 * If both graphs do not share an edge, the child has a 50 - 50 chance to inherit
	 * that edge. 
	 * 
	 * In some cases the child will not get all of the above edges, in order to keep the child's
	 * degree below the maximum limitation.
	 * @param parent1
	 * @param parent2
	 * @return child, a graph compromising the child of parent1 and parent2
	 */
	private static Graph crossover(Graph parent1, Graph parent2){
		Cell one = parent1.getEdgeCell();
		one.sortBySize();
		Cell two = parent2.getEdgeCell();
		two.sortBySize();
		int indexOne = 0;
		int indexTwo = 0;
		
		Cell childCell = new Cell();
		childCell.setNumPorts( rank );
		
		//must set number of nodes at end of method, for now it's set to max of both parents
		//fitness assigned to zero in constructor
		Graph child = new Graph("C("+parent1.getName()+")("+parent2.getName()+")", rank, 
				0, childCell);
		child.setNumNodes( Math.max(parent1.getNumNodes(), parent2.getNumNodes()) );
		
		//iterate through both edges
		while(indexOne != one.size() && indexTwo != two.size() ){
			BitVector edge = one.getPA()[indexOne];
			BitVector edge2 = two.getPA()[indexTwo];
			
			//if they both have that edge
			//child gets it
			if( edge.equals( edge2 ) ){
				if( !child.isBadEdge( edge )){
					child.addEdge(edge);
				}
				indexOne++;
				indexTwo++;
			} 
			//if only one of them has the edge
			else{
				//since edges are sorted by size
				//the smaller edge we know for sure is not in the other graph
				//only increment the graph with the smaller edge
				BitVector smallerEdge;
				if( edge.getNumber() > edge2.getNumber() ){
					smallerEdge = edge2;
					indexTwo++;
				} else{
					smallerEdge = edge;
					indexOne++;
				}
				//50-50 chance to get this edge
				if( random.nextDouble() < 0.50){
					if( !child.isBadEdge( smallerEdge )){
						child.addEdge( smallerEdge);
					}
				}
			}
			
		}
		//for every remaining edge
		//we must get 50 50 chance to add
		//every remaining in first
		for(int i = 0; indexOne < one.size(); indexOne++){
			BitVector edge = one.getPA()[indexOne];
			
			//50-50 chance to get this edge
			if( random.nextDouble() < 0.50){
				if( !child.isBadEdge( edge )){
					child.addEdge(edge);
				}
			}
		}
		//every remaining edge in second
		for(int i = 0; indexTwo < one.size(); indexTwo++){
			BitVector edge = one.getPA()[indexTwo];
			
			//50-50 chance to get this edge
			if( random.nextDouble() < 0.50){
				if( !child.isBadEdge( edge )){
					child.addEdge(edge);
				}
			}
		}
		
		//first node is numbered 0
		int numberNodes = childCell.getHighestNode() + 1;
		child.setNumNodes( numberNodes );
		
		return child;
	}
	
	/**
	 * Randomly mutates a graph for the genetic Algorithm. Note: the original
	 * graph is left untouched, a new graph is created. 
	 * 
	 * Graphs are mutated by the following operations
	 * 20% chance to add a node
	 * 20% chance (sort of) to remove a node
	 * 		- must ensure all edges going to that node deleted as well
	 * 40% chance to add a random edge
	 * 		- must ensure edge doesn't overflow the max degree restrictions
	 * 40% chance to remove a random edge
	 * 
	 * It's possible a graph goes through this method and doesn't get mutated at all
	 * 
	 * Added 5% chance to extend out the ports. 
	 * 
	 * @param starting, The Graph we want to mutate
	 * @return mutant, the mutated graph
	 */
	private static Graph mutateGraph(Graph starting){
		Graph mutant = new Graph(starting);
		//need to re-evalutae fitness
		mutant.setFitness( 0 );
		
		//add vertex
		if( random.nextDouble() < 0.20 && mutant.getNumNodes() < 30){
			mutant.setNumNodes( mutant.getNumNodes() + 1 );
		}
		//remove vertex
		//not precisely 20 percent chance to remove vertex
		//technically 0.80 * 0.20 chance
		//must ensure we can delete a node
		else if( random.nextDouble() < 0.20 && mutant.getNumNodes() > rank){
			//get random node
			//if port is deleted, node after that will be assigned new port automatically
			int node = 1 << random.nextInt( mutant.getNumNodes() );
			//and remove all edges with deleted vertex
			for(int i = 0; i < mutant.getEdgeCell().size(); i++){
				BitVector edge = mutant.getEdgeCell().getPA()[i];
				if(edge == null){
					System.out.println("WHAT IS HAPPENING");
				}
				if( edge.contains( node )){
					mutant.removeEdge( edge );
				}
			}
			mutant.setNumNodes( mutant.getNumNodes() - 1);
		}
		
		//add edge
		if( random.nextDouble() < 0.40 ){
			int node1 = 1 << random.nextInt( mutant.getNumNodes() );
			int node2 = 1 << random.nextInt( mutant.getNumNodes() );
			BitVector newEdge = new BitVector( node1 + node2 );
			
			int attempts = 0;
			//keep trying until edge is satisfactory
			//or dont' add any edge if you try over 15 times
			while( mutant.isBadEdge( newEdge ) && attempts < 15){
				node1 = 1 << random.nextInt( mutant.getNumNodes() );
				node2 = 1 << random.nextInt( mutant.getNumNodes() );
				newEdge = new BitVector( node1 + node2 );
				attempts++;
			}
			
			if( attempts < 15 ){
				mutant.addEdge( newEdge );
			}
			
		}

		
		//remove edge
		if( random.nextDouble() < 0.40 && mutant.getEdgeCell().size() > 0){
			BitVector removedEdge = mutant.getEdgeCell().getPA()[ random.nextInt(mutant.getEdgeCell().size()) ];
			mutant.removeEdge( removedEdge );
		}
		
		//extend the ports out
		if( random.nextDouble() < 0.05 && mutant.getNumNodes() < (30 - mutant.getNumPorts())){
			mutant = mutant.extendPortsNoCell();
		}
		return mutant;
	}
	
	/**
	 * Generates an initial population randomly. For each graph, the 
	 * the amount of nodes added ranges from 0 to 19
	 * 
	 * The amount of edges added ranges from number of nodes minus 1 to number of nodes
	 * plus 10.
	 * 
	 * @return Population object
	 */
	private static Population generateInitialPopulation(){
		
		ArrayList<Graph> population = new ArrayList<Graph>();
		
		//for each graph added
		for(int i = 0; i < POPULATION_SIZE; i++){
			
			gat.setProgressBar( (int) (((double)(i+1) / (double)POPULATION_SIZE) * 100) );
			
			String name = "G" + (i+1);
			int nP = rank;
			int nC = rank;
			
			//add anywhere from 0 - 19 nodes
			nC += random.nextInt(20);
			
			//edges always added
			//add atleast enough to connect all your nodes
			//num nodes - 1
			int edgesToAdd = nC-1 + random.nextInt(10);
			
			Cell c = new Cell();
			c.setNumPorts( rank );
			
			//the graph
			Graph newbie = new Graph(name, nP, nC, c);	
			
			//loop adding all the edges
			//care must be taken that
			// we don't add an edge we already have
			// the bit Vector generated is a valid edge
			// the edge doesn't overflow the max degree allocated
			innerloop:
			for(int j = 0; j < edgesToAdd; j++){
				int node1 = 1 << random.nextInt(nC);
				int node2 = 1 << random.nextInt(nC);
				
				BitVector bv = new BitVector( node1 + node2 );
				
				int attempts = 0;
				while( newbie.isBadEdge(bv) && attempts < 15 ){
					node1 = 1 << random.nextInt(nC);
					node2 = 1 << random.nextInt(nC);
					bv = new BitVector( node1 + node2 );
					attempts++;
				}
				
				if( attempts < 15){
					newbie.addEdge( bv );
				} else{
					break innerloop;
				}
			}
			
			//calculate the fitness of all new graphs
			calculateFitness(newbie);
			if( newbie.getFitness() < 0){
				i--;
			} else{
				population.add(newbie);
			}
			
		}
		
		return new Population(population);
	}
}
