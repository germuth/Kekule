package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class ParameterPanel extends JPanel{
	
	private LabeledSlider iterations;
	private LabeledSlider answerSize;
	private LabeledSlider popSize;
	private LabeledSlider elite;
	private LabeledSlider random;
	private LabeledSlider mutant;
	private LabeledSlider crossover;
	
	public ParameterPanel(MainWindow frame){
		super();
		
		this.setLayout( new GridLayout(8, 1));
		this.setSize( new Dimension(200, 750));
		Border lowered = BorderFactory.createLoweredBevelBorder();
		this.setBorder( lowered );
		
		iterations = new LabeledSlider("Iterations     ", 1, 1000, 100);
		answerSize = new LabeledSlider("Answer Size    ", 1, 50, 10);
		popSize = new LabeledSlider(   "Population Size", 1, 1000, 1000);	
		//TODO make the numbers scale when popsize changes
		elite = new LabeledSlider(     "Elite          ", 1, 1000, 170);
		random = new LabeledSlider(    "Random         ", 1, 1000, 30);
		mutant = new LabeledSlider(    "Mutant         ", 1, 1000, 400);
		crossover = new LabeledSlider( "Crossover      ", 1, 1000, 400);
		
		this.add( iterations, BorderLayout.WEST );
		this.add( answerSize, BorderLayout.WEST );
		this.add( popSize, BorderLayout.WEST );
		this.add( elite, BorderLayout.WEST );
		this.add( random, BorderLayout.WEST );
		this.add( mutant, BorderLayout.WEST );
		this.add( crossover, BorderLayout.WEST );
		
		this.add( new RunPanel(frame) );
	}
}
