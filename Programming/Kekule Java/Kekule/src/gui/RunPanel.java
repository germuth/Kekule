package gui;

import geneticAlgorithm.GeneticAlgorithm;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import shared.Cell;
import shared.InputParser;

public class RunPanel extends JPanel{
	private JButton cellButton;
	private JButton runButton;
	private Cell selected;
	
	private MainWindow frame;
	
	public RunPanel(final MainWindow frame){
		super();
		
		this.frame = frame;
		
		this.setSize( new Dimension(200, 100));
		selected = null;
		
		cellButton = new JButton("Select Cell");
		cellButton.addActionListener( new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String rankString = JOptionPane.showInputDialog("Rank?");
				int rank = Integer.parseInt( rankString );
				
				String number = JOptionPane.showInputDialog("Classification?");
				int classification = Integer.parseInt( number );
				
				selected = readClassification(rank, classification);
			}
			
		});
		
		runButton = new JButton("Run");
		runButton.addActionListener( new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String[] args = new String[5];
				GeneticAlgorithm.maine( RunPanel.this.selected, frame );
			}
			
		});
		
		this.add( cellButton );
		this.add( runButton );
	}
	
	public static Cell readClassification(int rank, int classification) {
		// reading classification
		File f = new File("FullClassificationRank" + rank + ".txt");
		Scanner s = null;
		try {
			s = new Scanner(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.nextLine();
		s.nextLine();
		s.nextLine();


		int count = 1;
		Cell input = null;
		try {
			input = InputParser.readCell2(s, rank);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (input != null) {
			if( count == classification){
				return input;
			} else{
				count++;
			}

			try {
				input = InputParser.readCell2(s, rank);
			} catch (Exception e) {
				input = null;
			}
		}
		
		return null;
	}
}
