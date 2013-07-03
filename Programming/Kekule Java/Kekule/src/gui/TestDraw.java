package gui;

import graphs.Graph;

import java.util.Scanner;

import javax.swing.JFrame;

import shared.InputParser;

public class TestDraw {

	public static void main(String[] args) { 
        JFrame frame = new JFrame(); 
        frame.setSize(300,300); 
        frame.setTitle("Two Square Viewer"); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
        Graph g = InputParser.readGraph( new Scanner(System.in) );
        
        GraphViewer component = new GraphViewer(g);  
        frame.add(component); 
         
        frame.setVisible(true); 
    } 
}
