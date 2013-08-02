package newGui;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;

/**
 * This class is meant to be a small JPanel which
 * contains two JLabels which change depending on the current
 * task being executed (Genetic Algorithm) and then a JProgressBar
 * to display the actual precentage
 * @author Aaron
 *
 */
public class LoadingBar extends JPanel {
	/**
	 * The JProgress bar which shows the actual percentage at any give time
	 */
	private JProgressBar progressBar;
	/**
	 * The upper most JLable, displaying the current task
	 */
	private JLabel currentTask;
	/**
	 * A textual indication of the current progress through the current task
	 * we are. For example
	 * 7 out of 14 graphs
	 */
	private JLabel currentProgress;
	/**
	 * What stage the loading bar is currenting in
	 */
	private int stage;
	/**
	 * The total populaiton Size. This is used to deterimine
	 * the percent through the inital pop geneartion we are 
	 * at any point
	 */
	private int popSize;
	
	/**
	 * Constructor,
	 * Creates the panel.
	 */
	public LoadingBar(int popSize) {
		setLayout(null);
		
		this.popSize = popSize;
		this.stage = 0;
		
		this.progressBar = new JProgressBar(0, 100);
		this.progressBar.setValue(0);
		this.progressBar.setStringPainted(true);
		progressBar.setBounds(10, 40, 75, 30);
		add(progressBar);
		
		this.currentTask = new JLabel("");
		currentTask.setFont(new Font("Tahoma", Font.PLAIN, 10));
		currentTask.setForeground(Color.GRAY);
		currentTask.setBounds(10, 0, 75, 14);
		add(currentTask);
		
		this.currentProgress = new JLabel("");
		currentProgress.setFont(new Font("Tahoma", Font.PLAIN, 10));
		this.currentProgress.setForeground(Color.GRAY);
		this.currentProgress.setBounds(10, 15, 75, 20);
		add( this.currentProgress );
	}
	
	/**
	 * Sets the currest stage we are not.
	 * Not exactly working TODO
	 * @param stage
	 */
	public void setStage(int stage){
		if(stage == 1){
			this.currentTask.setText("Generating Population...");
			this.stage = 1;
		} else if(stage == 2){
			this.currentTask.setText("Running Algorithm...");
			this.stage = 2;
		} else{
			this.currentTask.setText("Not currently running");
			this.stage = 0;
		}
	}
	
	/**
	 * Updates the current progress on this loading bars 
	 * JProgress bar
	 * @param progress, current percetage complete
	 */
	public void updateProgress(int progress){
		int currentStage = this.stage;
		
		if(this.stage == 0){
			this.setStage(1);
		}
		if( currentStage == 1){
			this.currentProgress.setText("Graph " + progress + " / " + this.popSize + "...");
			this.progressBar.setValue(progress);
			if(progress == 100){
				this.setStage(2);
			}
		} else if(currentStage == 2){
			this.currentProgress.setText("Found " + progress + " graphs out of " + 10 + "...");
			this.progressBar.setValue(progress);
			if(progress == 100){
				this.setStage(0);
			}
		}
	}

}
