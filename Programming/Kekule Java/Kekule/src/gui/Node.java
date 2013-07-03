package gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import shared.Utils;
import shared.Utils.Pair;

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
	
	//breadth first searches the graph for the shortest cycle length if any from this node to node n
	public int cycleLength(){
		
		//holds whether we already visited this node
		HashMap<Node, Boolean> reached = new HashMap<Node, Boolean>();
		
		//start searching at node 1
		LinkedList<Pair<Node, Integer>> openList = new LinkedList<Pair<Node, Integer>>();
		openList.add( new Pair<Node, Integer>(this, 0) );
		
		while( !openList.isEmpty() ){
			
			
			Pair<Node, Integer> p = openList.pop();
			Node parent = p.getFirst();
			//if we've already visited this node
			if( reached.get(parent) != null ){
				continue;
			}
			reached.put( parent, true );
			
			//search all neighbors
			for(Node y : parent.neighbours){
				
				if( y.equals(this) && p.getSecond() > 1){
					return p.getSecond() + 1;
				}
				
				if( !reached.containsKey(y) ){
					openList.add( new Pair<Node, Integer>( y, p.getSecond() + 1) );
				}
				
			}
		}
		return 0;
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
