package newGui;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;

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
