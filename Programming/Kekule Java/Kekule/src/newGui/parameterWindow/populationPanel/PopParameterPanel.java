package newGui.parameterWindow.populationPanel;

import geneticAlgorithm.GAParameters;
import gui.LabeledSlider;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
/**
 * PopParameterPanel
 * 
 * This JPanel contains LabeledSlider's for each parameter of the population. More specifically,
 * the size of the population, and what makes up each next generation population.
 * @author Aaron
 *
 */
public class PopParameterPanel extends JComponent {
	/**
	 * Everything is added to this internal JPanel
	 */
	private JPanel contentPane;
	/**
	 * Holds the total size of a population
	 */
	private LabeledSlider popSize;
	/**
	 * The amount of graphs from last population who survive to the next population, 
	 * based purely on the graphs with the most fitness
	 */
	private LabeledSlider elite;
	/**
	 * The amount of graphs from last population who are randomly selected to live on.
	 * This is kept low, but still present to ensure genetic diversity
	 */
	private LabeledSlider random;
	/**
	 * The amount of new graphs generated from mutation each iteration
	 */
	private LabeledSlider mutant;
	/**
	 * The amount of new graphs generated from crossover each iteration
	 */
	private LabeledSlider crossover;

	/**
	 * Create the frame.
	 */
	public PopParameterPanel() {
		this.setPreferredSize(new Dimension(297, 170));
		setLayout(new GridLayout(0, 1, 0, 0));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		contentPane.setLayout(new GridLayout(6, 1, 0, 0));
		
		JLabel lblGeneticAlgorithmParameter = new JLabel("Iterative Population:");
		lblGeneticAlgorithmParameter.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblGeneticAlgorithmParameter);
		
		this.popSize = new LabeledSlider("Population Size", 1, 2500, 1000);
		contentPane.add( this.popSize );
	
		this.elite = new LabeledSlider("Elite", 1, 2500, 170);
		contentPane.add( this.elite );
		
		this.random = new LabeledSlider("Random", 1, 2500, 30);
		contentPane.add( this.random );		

		this.mutant = new LabeledSlider("Mutant", 1, 2500, 400);
		contentPane.add( this.mutant );	

		this.crossover = new LabeledSlider("Crossover", 1, 2500, 400);
		contentPane.add( this.crossover );		

		this.add( contentPane );
	}
	
	/**
	 * Reads all parameters in this, and sets them in the genetic algorithm
	 */
	public void setParameters(){
		
		GAParameters.setPopulationSize( this.popSize.getValue() );
		
		GAParameters.setEliteNumber( this.elite.getValue() );
		GAParameters.setRandomNumber( this.random.getValue() );
		
		GAParameters.setMutantNumber( this.mutant.getValue() );
		GAParameters.setCrossoverNumber( this.crossover.getValue() );
	}
}
