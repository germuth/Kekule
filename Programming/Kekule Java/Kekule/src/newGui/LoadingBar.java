package newGui;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import java.awt.Color;

public class LoadingBar extends JPanel {

	private JProgressBar progressBar;
	
	private JLabel currentTask;
	
	private JLabel currentProgress;
	
	private int stage;
	
	private int totalIterations;
	
	private int popSize;
	/**
	 * Create the panel.
	 */
	public LoadingBar(int totalIterations, int popSize) {
		setLayout(null);
		
		this.totalIterations = totalIterations;
		this.popSize = popSize;
		
		this.progressBar = new JProgressBar();
		progressBar.setBounds(3, 40, 146, 30);
		add(progressBar);
		
		this.currentTask = new JLabel("Not Currently Running");
		currentTask.setForeground(Color.GRAY);
		currentTask.setBounds(19, 0, 130, 14);
		add(currentTask);
		
		JLabel currentProgress = new JLabel("Graph 0 / 1000...");
		currentProgress.setForeground(Color.GRAY);
		currentProgress.setBounds(28, 20, 97, 14);
		add(currentProgress);
	}
	
	public void setStage(int stage){
		if(stage == 1){
			this.currentTask.setText("Generating Population...");
		} else if(stage == 2){
			this.currentTask.setText("Running Algorithm...");
		} else{
			this.currentTask.setText("Not currently running");
		}
	}
	
	public void updateProgress(int progress){
		
	}

}
