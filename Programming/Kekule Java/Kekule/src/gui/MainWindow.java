/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import cdk.ImageRenderer;

/**
 *
 * @author Aaron
 */
public class MainWindow extends javax.swing.JFrame {
	private ImageRenderer imageRenderer;
	
    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    public void giveSMILES(ArrayList<String> list){
    	this.imageRenderer.setGraphs( list );
    }
   
	private void initComponents() {
		this.setSize(new Dimension(950, 545));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		ParameterPanel p = new ParameterPanel();
		
		JPanel contents = new JPanel();
		contents.setLayout( new GridLayout(1, 2) );
		
		ArrayList<String> smiles = new ArrayList<String>();
		smiles.add( "O=Cc1ccc(O)c(OC)c1" );
		smiles.add( "CC(=O)NCCC1=CNc2c1cc(OC)cc2" );
		smiles.add( "CCc(c1)ccc2[n+]1ccc3c2Nc4c3cccc4" );
		
		imageRenderer = new ImageRenderer(smiles);
		
		contents.add( imageRenderer, BorderLayout.CENTER );

		JPanel right = new JPanel();
		
		JTextArea intro = new JTextArea("Welcome to Interactive Kekule Theory. \n \n Here we search for graphs which" +
				" not only match Kekule Cells, but real stable hydrocarbons as well. Begin by entering a rank" +
				" and \n classification with the buttons below.");
		intro.setLineWrap(true);
		intro.setFont(new Font("Serif", Font.PLAIN, 14));
		
		intro.setSize( new Dimension( 400, 400 ));
		intro.setBackground( Color.white );
		
		//JPanel spacer = new JPanel();
		//spacer.add( new JLabel("H") );
		//spacer.setSize( new Dimension(200, 100) );
		//spacer.setMinimumSize( new Dimension(200, 50));
		
		right.add( Box.createRigidArea( new Dimension(200, 30)));
		//right.add( spacer );
		right.add( intro );
		right.add( Box.createRigidArea( new Dimension(200, 30)));
		//right.add( spacer );
		right.add( p );
		//right.add( spacer );
		right.add( new RunPanel(this) );
		
		contents.add( right );
		
		this.add( contents );
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        	
        }
           
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
}
