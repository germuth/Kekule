package newGui.parameterWindow;

import geneticAlgorithm.GAParameters;
import gui.LabeledSlider;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;

import shared.Utils;
/**
 * Mutation Window
 * 
 * This class is a pop up window which contains LabeledSliders for each parameter of
 * mutation within the genetic algorithm. This includes the percentage chance that each
 * possible mutation will be applied.
 * 
 * @author Aaron
 */
public class MutationWindow extends JFrame {
	/**
	 * JPanel which holds all contents of this frame
	 */
	private JPanel contentPane;
	/**
	 * Holds the percentage chance to add a node
	 */
	private LabeledSlider addN;
	/**
	 * Holds the percentage chance to remove a node
	 */
	private LabeledSlider removeN;
	/**
	 * Holds the percentage chance to add an edge
	 */
	private LabeledSlider addE;
	/**
	 * Holds the percentage chance to remove an edge
	 */
	private LabeledSlider removeE;
	/**
	 * Holds the percentage chance to extend all ports of the graph
	 */
	private LabeledSlider extendP;
	

	/**
	 * Create the frame.
	 */
	public MutationWindow() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 397, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(7, 1, 0, 0));
		
		JLabel lblGeneticAlgorithmParameter = new JLabel("Mutation Parameters:");
		lblGeneticAlgorithmParameter.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblGeneticAlgorithmParameter);
		
		this.addN = new LabeledSlider("Add Node %", 0, 100, 20);
		contentPane.add( this.addN );
	
		this.removeN = new LabeledSlider("Remove Node %", 0, 100, 20);
		contentPane.add( this.removeN );
		
		this.addE = new LabeledSlider("Add Edge %", 0, 100, 20);
		contentPane.add( this.addE );
		
		this.removeE = new LabeledSlider("Remove Edge %", 0, 100, 20);
		contentPane.add( this.removeE );
		
		this.extendP = new LabeledSlider("Extend Ports %", 0, 100, 5);
		contentPane.add( this.extendP );
		
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(20, 5, 267, 30);
		panel_1.setPreferredSize(new Dimension(267, 30));
		panel.add(panel_1);
	
		
		JButton btnNewButton = new JButton("Close");
		btnNewButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MutationWindow.this.setVisible(false);
				MutationWindow.this.setParameters();
			}	
		});
		btnNewButton.setBounds(283, 12, 78, 23);
		panel.add(btnNewButton);

	}
	
	/**
	 * Takes all values in this window, and sets them in GAParameters
	 */
	public void setParameters(){
		
		GAParameters.setAddNodeChance( Utils.percent( this.addN.getValue() ) );
		GAParameters.setRemoveNodeChance( Utils.percent( this.removeN.getValue() ) );
		
		GAParameters.setAddEdgeChance( Utils.percent( this.addE.getValue() ) );
		GAParameters.setRemoveEdgeChance( Utils.percent( this.removeE.getValue() ) );
		
		GAParameters.setExtendPortsChance( Utils.percent( this.extendP.getValue() ) );
	}
}
