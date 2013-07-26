package newGui.ParameterWindows.PopulationPanel;

import gui.LabeledSlider;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSeparator;

public class InitPopParameterPanel extends JComponent {

	private JPanel contentPane;
	
	private LabeledSlider popSize;
	private LabeledSlider elite;
	private LabeledSlider random;
	private LabeledSlider mutant;
	private LabeledSlider crossover;
	private JPanel panel;
	private JPanel panel_1;
	private JButton btnNewButton;

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
		
		this.popSize = new LabeledSlider("Number Nodes from", 0, 25, 0);
		contentPane.add( this.popSize );
	
		this.elite = new LabeledSlider("Number Nodes to", 1, 30, 20);
		contentPane.add( this.elite );
		
		this.random = new LabeledSlider("Number Edges from", 5, 20, 5);
		contentPane.add( this.random );		

		this.mutant = new LabeledSlider("Number Edges to", 5, 30, 30);
		contentPane.add( this.mutant );	

		this.crossover = new LabeledSlider("Fitness Threshold", -10, 10, 0);
		contentPane.add( this.crossover );		
		
		this.add(contentPane);
		contentPane.setPreferredSize(new Dimension( 297, 200));
	}
	
}
