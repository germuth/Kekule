package gui;

import graphs.Graph;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;

import shared.BitVector;
import shared.Cell;

public class GraphViewer extends JComponent {
	private static final int X_FIRST = 50;
	private static final int Y_FIRST = 50;
	
	private Graph myGraph;
	private Map<Integer, Node> nameToNode;
	private int lastX;
	private int lastY;
	
	public GraphViewer(Graph g){
		super();
		
		this.myGraph = g;
		this.nameToNode = new HashMap<Integer, Node>();
		this.lastX = X_FIRST;
		this.lastY = Y_FIRST;
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		
		boolean oneAdded = false;
		boolean twoAdded = false;
		
		Cell edges = myGraph.getEdgeCell();
		for(int i = 0; i < edges.size(); i++){
			BitVector edge = edges.getPA()[i];
			
			int node1 = edge.firstBit();
			Node one = this.nameToNode.get( node1 );
			if( one == null ){
				one = new Node(lastX, lastY, false, node1 );
				this.nameToNode.put( one.getNumber(), one );
				oneAdded = true;
				
				lastX += 20;
				if(lastY == 70){
					lastY -= 20;
				}
				else{
					lastY += 20;
				}
			}
			
			
			int node2 = new BitVector( edge.getNumber() - ( 1 << node1 ) ).firstBit();
			//if node2 not exist
			Node two = this.nameToNode.get( node2 );
			if( two == null ){
				two = new Node(lastX, lastY, false, node2 );
				this.nameToNode.put( two.getNumber(), two );
				twoAdded = true;
				
				lastX += 20;
				if(lastY == 70){
					lastY -= 20;
				}
				else{
					lastY += 20;
				}
			}
			
			
			//add neighbours
			one.addNeighbour(two);
			two.addNeighbour(one);
			
			ArrayList<Node> cycle = null;
			if( oneAdded){
				cycle = one.getCycle();
			} else if( twoAdded ){
				cycle = two.getCycle();
			} else{
				cycle = new ArrayList<Node>();
			}
			if( !cycle.isEmpty() ){
				if( cycle.size() ==7){
					Node start = cycle.get(0);
					Node second = cycle.get(1);
					second.translate(start.getX() + 20, start.getY() + 20);
					Node three = cycle.get(2);
					three.translate(second.getX(), second.getY() + 20);
					Node four = cycle.get(3);
					four.translate( three.getX() - 20, three.getY() + 20);
					Node five = cycle.get(4);
					five.translate( three.getX() - 40, three.getY() );
					Node six = cycle.get(5);
					six.translate( five.getX(), five.getY() - 20);
				}
				
			}
			
		}
		
		Iterator<Node> it = this.nameToNode.values().iterator();
		
		while( it.hasNext() ){
			Node current = it.next();
			
			g2.drawString( current.getNumber() + "", current.getX() + 10, current.getY() + 20 );
			g2.fillOval(current.getX(), current.getY(), 10, 10);
		}
		
		for(int i = 0; i < edges.size(); i++){
			BitVector edge = edges.getPA()[i];
			
			int node1 = edge.firstBit();
			int node2 = new BitVector( edge.getNumber() - ( 1 << node1 ) ).firstBit();
			
			Node one = this.nameToNode.get(node1);
			Node two = this.nameToNode.get(node2);
			
			g2.drawLine( one.getX() + 5, one.getY() + 5, two.getX() + 5, two.getY() + 5 );
		}
	}
	
}
