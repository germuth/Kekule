package geneticAlgorithm;

import graphs.Graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import shared.BitVector;
import shared.Cell;
import shared.GraphtoCell;
import shared.InputParser;

public class GeneticAlgorithm {
	private static final int POPULATION_SIZE = 500;
	private static final int ITERATIONS = 500;
	public static final int ELITE_NUMBER = 90;
	public static final int RANDOM_NUMBER = 10;
	private static final int MUTANT_NUMBER = 200;  
	private static final int CROSSOVER_NUMBER = 200; 
	private static final Random random = new Random();
	
	private static int rank;
	private static Cell cell;
	public static void main(String[] args){
		
		Scanner input = new Scanner(System.in);
		
		cell = InputParser.readCell(input);
		cell.normalize();
		
		rank = cell.getNumPorts();
		
		Population population = generateInitialPopulation( rank );
		ArrayList<Graph> nextGen = null;
		for(int i = 0; i < ITERATIONS; i++){
			nextGen = population.getNextGeneration();
			
			//mutate 200 random Graphs and add to nextGen
			//since we're picking from 100 elites
			//likely to get each graph twice
			//must add mutants to separate list to ensure they are not crossovered this iteration
			ArrayList<Graph> mutants = new ArrayList<Graph>();
			for(int j = 0; j < MUTANT_NUMBER; j++){
				Graph mutant = nextGen.get( random.nextInt( nextGen.size()) );
				mutants.add( mutateGraph(mutant) );
			}
			
			for(int j = 0; j < CROSSOVER_NUMBER; j++){
				//TODO use 4 parents
				Graph parent1 = nextGen.get( random.nextInt( nextGen.size()) );
				Graph parent2 = nextGen.get( random.nextInt( nextGen.size()) );
				
				Graph child = crossover(parent1, parent2);
				nextGen.add(child);
			}
			nextGen.addAll(mutants);
			
			if(nextGen.size() != 500){
				System.out.println("ERROR");
			}
			
			//fitness function on every graph
			for(int j = 0; j < nextGen.size(); j++){
				Graph current = nextGen.get(j);
				calculateRank( current );
			}
		}
		Population finalPop = new Population(nextGen);
		finalPop.getBest().writeGraph();
		
	}
	
	public static void calculateRank( Graph g){
		Cell answer = cell;
		answer.sortBySize();
		
		Cell gsCell = GraphtoCell.makeCell( g );
		gsCell.normalize();
		gsCell.sortBySize();
		
		int answerIndex = 0;
		int gsIndex = 0;
		
		int rank = 0;
		
		for(int i = 0; i < Math.max(answer.size(), gsCell.size()); i++){
			BitVector correct = answer.getPA()[answerIndex];
			BitVector gBV = gsCell.getPA()[gsIndex];
			
			//if it has the port assignment
			if( correct.equals(gBV) ){
				rank++;
				
				answerIndex++;
				gsIndex++;
			}
			else{
				rank--;
				//if it doesn't have the port assignment
				if( correct.getNumber() < gBV.getNumber() ){
					answerIndex++;
				}
				//if it has extra port assignment
				else{
					gsIndex++;
				}
			}
			
		}
		g.setRank( rank );
	}
	
	private static Graph crossover(Graph parent1, Graph parent2){
		Cell one = parent1.getEdgeCell();
		one.sortBySize();
		Cell two = parent2.getEdgeCell();
		two.sortBySize();
		int indexOne = 0;
		int indexTwo = 0;
		
		Set<BitVector> childEdges = new HashSet<BitVector>();
		
		for(int i = 0; i < Math.max(one.size(), two.size()); i++){
			BitVector edge = one.getPA()[indexOne];
			BitVector edge2 = two.getPA()[indexTwo];
			//if they both have that edge
			//take it
			if( edge.equals( edge2 )){
				childEdges.add(edge);
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
					childEdges.add( smallerEdge );
				}
			}
			
		}
		
		Cell childCell = new Cell(childEdges, rank);
		
		Graph child = new Graph("Childof"+parent1.getName()+"and"+parent2.getName(), rank, 
				Math.max(parent1.getNumNodes(), parent2.getNumNodes()), childCell);
		return child;
	}
	
	private static Graph mutateGraph(Graph starting){
		Graph mutant = new Graph(starting);
		
		//add vertex
		if( random.nextDouble() < 0.20 ){
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
			mutant.addEdge( newEdge );
		}
		//remove edge
		if( random.nextDouble() < 0.40 ){
			int node1 = 1 << random.nextInt( mutant.getNumNodes() );
			int node2 = 1 << random.nextInt( mutant.getNumNodes() );
			BitVector newEdge = new BitVector( node1 + node2 );
			mutant.removeEdge( newEdge );
		}
		
		return mutant;
	}
	
	private static Population generateInitialPopulation(int rank){
		
		ArrayList<Graph> population = new ArrayList<Graph>();
		
		for(int i = 0; i < POPULATION_SIZE; i++){
			String name = "graph" + (i+1);
			int nP = rank;
			int nC = rank;
			if(random.nextDouble() < 0.30){
				//add 1 to 4 nodes
				nC += random.nextInt(3) + 1;
			}
			
			int edgesToAdd = random.nextInt(10);
			BitVector[] edges = new BitVector[edgesToAdd];
			for(int j = 0; j < edgesToAdd; j++){
				int node1 = 1 << random.nextInt(nC);
				int node2 = 1 << random.nextInt(nC);
				edges[j] = new BitVector( node1 + node2 );
			}
			Cell cell = new Cell(edges);
			cell.setNumPorts(rank);
			Graph g = new Graph(name, nP, nC, cell);
			
			calculateRank(g);
			
			population.add(g);
		}
		
		return new Population(population);
	}
}
