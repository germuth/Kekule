package newGui.parameterWindow;

import geneticAlgorithm.GAParameters;
import gui.LabeledSlider;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * GAWindow
 * 
 * THis JFrame is a pop up window containing parameters for the 
 * genetic algorithm. The give parameters methods actually 
 * sets the parameters
 * 
 * @author Aaron
 */
public class GAWindow extends JFrame {
	/**
	 * The main jpanel of this window
	 */
	private JPanel contentPane;
	/**
	 * The amount of maximum iterations the genetic algorithm will run
	 */
	private LabeledSlider iterations;
	/**
	 * The amount of graphs it needs to find before it will terminate, unless the 
	 * maximum iterations is reached
	 */
	private LabeledSlider answers;
	/**
	 * A check box holding whether fitness is calculated:
	 * (TRUE) On every 2nd iteration, or
	 * (FALSE) on every single iteration
	 */
	private JCheckBox fitness;

	/**
	 * Create the frame.
	 */
	public GAWindow() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 397, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(4, 1, 0, 0));
		
		JLabel lblGeneticAlgorithmParameter = new JLabel("Genetic Algorithm Parameters:");
		lblGeneticAlgorithmParameter.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblGeneticAlgorithmParameter);
		
		this.iterations = new LabeledSlider("Iterations", 1, 250, 100);
		contentPane.add( this.iterations );
	
		this.answers = new LabeledSlider("Answers", 1, 25, 10);
		contentPane.add( this.answers );
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(20, 5, 267, 30);
		panel_1.setPreferredSize(new Dimension(267, 30));
		panel.add(panel_1);
		
		this.fitness= new JCheckBox("Calculate Fitness Every 2nd Iteration?");
		panel_1.add(fitness);
		fitness.setHorizontalAlignment(SwingConstants.CENTER);
		
		JButton btnNewButton = new JButton("Close");
		btnNewButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GAWindow.this.setVisible(false);
				GAWindow.this.giveParameters();
			}	
		});
		btnNewButton.setBounds(283, 12, 78, 23);
		panel.add(btnNewButton);	
	}
	
	/**
	 * Sets all parameters changed in this window
	 */
	public void giveParameters(){
		//set iteration number
		GAParameters.setIterations( this.iterations.getValue() );
		GAParameters.setMinimumGraphsRequired( this.answers.getValue() );
		GAParameters.setCalculateFitnessEvery2nd( this.fitness.isSelected() );
	}
}
