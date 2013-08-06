package gui.parameterWindow.populationPanel;

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
 * This class is a JPanel which contains LabeledSlider's for each parameter in the 
 * creation of a new starting population. 
 * 
 * @author Aaron
 *
 */
public class InitPopParameterPanel extends JComponent {
	/**
	 * The panel that everything is added to
	 */
	private JPanel contentPane;
	/**
	 * LabeledSlider which holds the minimum amount of nodes in any created graph
	 */
	private LabeledSlider numNodesFrom;
	/**
	 * LabeledSlider which holds the maximum amount of nodes in any created graph
	 */
	private LabeledSlider numNodesTo;
	/**
	 * Holds the minimum amount of edges in any created graph
	 */
	private LabeledSlider numEdgesFrom;
	/**
	 * Holds the maximum amount of edges in any created graph
	 */
	private LabeledSlider numEdgesTo;
	/**
	 * Holds the fitness threshold, all created graphs are tested for their fitness, and
	 * all graphs with fitness below this threshold will be throw out, and a new graph
	 * will be generated instead. 
	 */
	private LabeledSlider fitnessThreshold;

	/**
	 * Create the frame.
	 */
	public InitPopParameterPanel() {
		this.setPreferredSize(new Dimension( 297, 200));
		setLayout(new GridLayout(0, 1, 0, 0));
		//setPreferredSize(100, 100, 397, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		contentPane.setLayout(new GridLayout(6, 1, 0, 0));
		
		JLabel lblGeneticAlgorithmParameter = new JLabel("Initial Population:");
		lblGeneticAlgorithmParameter.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblGeneticAlgorithmParameter);
		
		this.numNodesFrom = new LabeledSlider("Number Nodes from", 0, 25, 0);
		contentPane.add( this.numNodesFrom );
	
		this.numNodesTo = new LabeledSlider("Number Nodes to", 1, 30, 20);
		contentPane.add( this.numNodesTo );
		
		this.numEdgesFrom = new LabeledSlider("Number Edges from", 0, 20, 0);
		contentPane.add( this.numEdgesFrom );		

		this.numEdgesTo = new LabeledSlider("Number Edges to", 1, 30, 25);
		contentPane.add( this.numEdgesTo );	

		this.fitnessThreshold = new LabeledSlider("Fitness Threshold", -15, 15, 0);
		contentPane.add( this.fitnessThreshold );		
		
		this.add(contentPane);
		contentPane.setPreferredSize(new Dimension( 297, 200));
	}
	
	/**
	 * This method graphs the values from this component, and changes the
	 * parameters of the genetic algorithm
	 */
	public void setParameters(){
		
		GAParameters.setNumNodesFrom( this.numNodesFrom.getValue() );
		GAParameters.setNumNodesTo( this.numNodesTo.getValue() );
		
		GAParameters.setNumEdgesFrom( this.numEdgesFrom.getValue() );
		GAParameters.setNumEdgesTo( this.numEdgesTo.getValue() );
		
		GAParameters.setFitnessThreshold( this.fitnessThreshold.getValue() );
	}
}
