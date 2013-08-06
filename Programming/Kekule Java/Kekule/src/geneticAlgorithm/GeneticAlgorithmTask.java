package geneticAlgorithm;

import gui.MainFrame;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import shared.Cell;
import shared.InputParser;

/**
 * Genetic Algorithm
 * 
 * First, an explanation on why a swing worker is used:
 * (stolen from the Internet)
 * Generally, SwingWorker is used to perform long-running tasks in Swing.
 * Running long-running tasks on the Event Dispatch Thread (EDT) can cause 
 * the GUI to lock up, so one of the things which was done is to use 
 * SwingUtilities.invokeLater and invokeAndWait which keeps the GUI 
 * responsive by which prioritizing the other AWT events before running 
 * the desired task (in the form of a Runnable).
 * 
 * However, the problem with SwingUtilities is that it didn't 
 * allow returning data from the the executed Runnable to the
 * original method. This is what SwingWorker was designed to address.
 * 
 * Therefore, we use a swing worker to execute the (long-running) genetic algorithm,
 * which we can than use to yield a list of graphs (in SMILES notation)
 * @author Aaron
 */
public class GeneticAlgorithmTask extends SwingWorker<ArrayList<String>, Integer> {
	/**
	 * A cell object of the cell of this.rank and this.classification. This cell is obtained
	 * by reading from the full classification text documents
	 */
	private Cell cell;
	/**
	 * The main graphical user interface. A reference is stored here, so when the task
	 * has finished executing, it can update the results in the user interface
	 */
	private MainFrame main;
	/**
	 * The rank (number of ports) we are giving to the genetic algorithm to search for
	 */
	private int rank;
	/**
	 * The classification number (from Hesselink's classification) that we are giving
	 * to the genetic algorithm to search for
	 */
	private int classification;
	
	/**
	 * The main constructor for a genetic algorithm task
	 * @param rank, the rank of cell 
	 * @param classification, of the cell
	 * @param main, a reference to the MainFrame window 
	 */
	public GeneticAlgorithmTask(int rank, int classification, MainFrame main){
		this.main = main;
		this.rank = rank;
		this.classification = classification;

		this.setProgress(0);
		
		ArrayList<Cell> classifications = InputParser.readClassification(rank);
		this.cell = classifications.get(classification - 1);
	}

    /**
     * Starts the genetic algorithm in an alternate thread
     */
    @Override
    public ArrayList<String> doInBackground() {
      
        setProgress(0);
        return GeneticAlgorithm.setUpAndRun( this.cell, this );
    }

    /**
     * Retrieves the results from the genetic algorithm. This thread is called
     * by the event-dispatching thread, not the alternate one used to compute
     * the genetic algorithm
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
    
    /**
     * Set's the progress of the task, which is in turned used to display a 
     * loading bar for the genetic algorithm as it runs
     * @param progress, the current percentage complete (0-100)
     */
    public void setProgressBar(int progress){
		this.setProgress(progress);
	}
}
