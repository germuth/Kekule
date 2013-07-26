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

public class PopParameterPanel extends JComponent {

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
}
