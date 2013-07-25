package gui;

import geneticAlgorithm.GeneticAlgorithm;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import shared.Cell;
import shared.InputParser;

public class RunPanel extends JPanel{
	private JButton cellButton;
	private JButton runButton;
	private ArrayList<Cell> classifications;
	private Cell selected;
	
	private MainWindow frame;
	
	public RunPanel(final MainWindow frame){
		super();
		
		this.frame = frame;
		
		this.setSize( new Dimension(200, 100));
		this.selected = null;
		
		cellButton = new JButton("Select Cell");
		cellButton.addActionListener( new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String rankString = JOptionPane.showInputDialog("Rank?");
				int rank = Integer.parseInt( rankString );
				
				String number = JOptionPane.showInputDialog("Classification?");
				int classification = Integer.parseInt( number );
				
				RunPanel.this.classifications = InputParser.readClassification(rank);
				selected = classifications.get(classification - 1);
			}
			
		});
		
		runButton = new JButton("Run");
		//runButton.addActionListener( new ActionListener(){

			//@Override
			//public void actionPerformed(ActionEvent e) {
			//	GeneticAlgorithm.setUpAndRun( RunPanel.this.selected, frame );
			//}
			
		//});
		
		this.add( cellButton );
		this.add( runButton );
	}
}
