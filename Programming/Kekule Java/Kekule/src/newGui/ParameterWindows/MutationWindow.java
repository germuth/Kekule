package newGui.ParameterWindows;

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

public class MutationWindow extends JFrame {

	private JPanel contentPane;
	
	private LabeledSlider addN;
	private LabeledSlider removeN;
	private LabeledSlider addE;
	private LabeledSlider removeE;
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
			}	
		});
		btnNewButton.setBounds(283, 12, 78, 23);
		panel.add(btnNewButton);

		
	}
}
