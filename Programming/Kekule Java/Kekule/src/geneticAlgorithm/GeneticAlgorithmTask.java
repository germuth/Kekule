package geneticAlgorithm;

import gui.RunPanel;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import shared.Cell;
import shared.InputParser;

public class GeneticAlgorithmTask implements Callable<ArrayList<String>>{
	private int rank;
	private int classification;
	private Cell cell;

	public GeneticAlgorithmTask(int rank, int classification){
		this.rank = rank;
		this.classification = classification;
		
		ArrayList<Cell> classifications = InputParser.readClassification(rank);
		this.cell = classifications.get(classification - 1);
	}
	
	@Override
	public ArrayList<String> call() throws Exception {
		return GeneticAlgorithm.setUpAndRun( this.cell );
	}

}
