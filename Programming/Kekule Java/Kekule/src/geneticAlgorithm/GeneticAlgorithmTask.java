package geneticAlgorithm;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import newGui.MainFrame;
import shared.Cell;
import shared.InputParser;

/**
 * Generally, SwingWorker is used to perform long-running tasks in Swing.
 * Running long-running tasks on the Event Dispatch Thread (EDT) can cause 
 * the GUI to lock up, so one of the things which were done is to use 
 * SwingUtilities.invokeLater and invokeAndWait which keeps the GUI 
 * responsive by which prioritizing the other AWT events before running 
 * the desired task (in the form of a Runnable).
 * 
 * However, the problem with SwingUtilities is that it didn't 
 * allow returning data from the the executed Runnable to the
 * original method. This is what SwingWorker was designed to address.
 * 
 * 
 * @author Aaron
 */

/*
public class GeneticAlgorithmTask extends SwingWorker<ArrayList<String>, Void>{
	private int rank;
	private int classification;
	private Cell cell;
	
	private LoadingBar loadingBar;

	public GeneticAlgorithmTask(int rank, int classification, LoadingBar loadingBar){
		this.rank = rank;
		this.classification = classification;
		this.loadingBar = loadingBar;
		
		this.setProgress(0);
		
		ArrayList<Cell> classifications = InputParser.readClassification(rank);
		this.cell = classifications.get(classification - 1);
	}

	@Override
	protected ArrayList<String> doInBackground() throws Exception {
		return GeneticAlgorithm.setUpAndRun( this.cell, this );
	}
	
	public void done(){
		Toolkit.getDefaultToolkit().beep();
	}

}
*/
public class GeneticAlgorithmTask extends SwingWorker<ArrayList<String>, Integer> {
	private Cell cell;
	private MainFrame main;
	private int rank;
	private int classification;
	
	public GeneticAlgorithmTask(int rank, int classification, MainFrame main){
		this.main = main;
		this.rank = rank;
		this.classification = classification;
		//this.loadingBar = loadingBar;
		
		this.setProgress(0);
		
		ArrayList<Cell> classifications = InputParser.readClassification(rank);
		this.cell = classifications.get(classification - 1);
	}

    /*
     * Main task. Executed in background thread.
     */
    @Override
    public ArrayList<String> doInBackground() {
      
        setProgress(0);
        return GeneticAlgorithm.setUpAndRun( this.cell, this );
    }

    /*
     * Executed in event dispatching thread
     */
    @Override
    public void done() {
    	try {
    		Toolkit.getDefaultToolkit().beep();
    		setProgressBar(100);
			ArrayList<String> graphs = this.get();
			String cell = "K" + this.classification + ": ";
			cell += this.cell.toString().substring(7);
			cell += " (" + this.rank + ")";
			main.collectAndShowResults(graphs, cell);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    public void setProgressBar(int progress){
		this.setProgress(progress);
	}
}
