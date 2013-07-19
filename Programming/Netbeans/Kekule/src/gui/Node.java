package gui;

import java.util.ArrayList;
import java.util.LinkedList;

import shared.Utils;
import shared.Utils.Pair;

/**
 * Represents a node of a graph for the visual portion of the application.
 * This will likely be replaced by 
 * OpenEye Scientific Software
 * OGHAM TK
 * 
 * @author Aaron
 *
 */
public class Node {
	private int x;
	private int y;
	private boolean port;
	private int number;
	private ArrayList<Node> neighbours;
	
	public Node(int x, int y, boolean port, int number){
		this.x = x;
		this.y = y;
		this.port = port;
		this.number = number;
		this.neighbours = new ArrayList<Node>();
	}
	
	public void addNeighbour(Node n){
		this.neighbours.add(n);
	}
	
	public void translate(int x, int y){
		this.setX( x );
		this.setY( y );
	}
	
	public boolean equals(Object o){
		Node another = (Node) o;
		if(this.number == another.number){
			return true;
		}
		return false;
	}
	
	//breadth first searches the graph for the shortest cycle length if any from this node to node n
	public ArrayList<Node> getCycle(){
		
		
		//start searching at node 1
		LinkedList<Pair<Node, ArrayList<Node>>> openList = new LinkedList<Pair<Node, ArrayList<Node>>>();
		ArrayList<Node> temp = new ArrayList<Node>();
		temp.add(this);
		openList.add( new Pair<Node, ArrayList<Node>>(this, temp) );
		
		while( !openList.isEmpty() ){
			
			
			Pair<Node, ArrayList<Node>> p = openList.pop();
			Node parent = p.getFirst();
			ArrayList<Node> packet = p.getSecond();
			
			if( parent.equals(this) && packet.size() > 1 ){
				return packet;
			}
			
			//search all neighbors
			for(Node y : parent.neighbours){
				
				//don't send packet back to sending neighbor
				if( packet.size() >= 2 && y.equals( packet.get( packet.size() - 2) ) ){
					continue;
				}
				
				//send packet everywhere else
				//as long as packet is below max length
				if( packet.size() < 8){
					ArrayList<Node> newT = new ArrayList<Node>(packet);
					newT.add(y);
					Pair<Node, ArrayList<Node>> newP = new Pair<Node, ArrayList<Node>>(y, newT);
					openList.add(newP);
				}
				
			}
		}
		return new ArrayList<Node>();
	}
	
	public String toString(){
		String s = "";
		s += this.number + "(";
		for( Node n: this.neighbours){
			s += n.number + ", ";
		}
		s += ")";
		return s;
		
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isPort() {
		return port;
	}

	public void setPort(boolean port) {
		this.port = port;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}


}
