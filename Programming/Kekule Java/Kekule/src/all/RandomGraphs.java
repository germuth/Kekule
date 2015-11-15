package all;
import graphs.Graph;

import java.util.ArrayList;
import java.util.Random;

import shared.BitVector;
import shared.Cell;
import shared.GraphtoCell;


public class RandomGraphs {
	public static Random rng = new Random();
	
	private static Graph tryToCreateGraph(int rank) {
		int nP = rank;
		int nC = rank;
		
		// add anywhere from (to) -> (from) nodes
		int from = 0;
		int to = 20;
		nC += from + rng.nextInt(to - from);

		// edges always added
		// add atleast enough to connect all your nodes
		// which is num nodes - 1
		// PLUS from -> to
		from = 0;
		to = 25;
		int edgesToAdd = nC - 1 + from + rng.nextInt(to - from);

		Cell c = new Cell();
		c.setNumPorts(nP);

		// the graph
		Graph newbie = new Graph("G", nP, nC, c);

		// loop adding all the edges
		// care must be taken that
		// we don't add an edge we already have
		// the bit Vector generated is a valid edge
		// the edge doesn't overflow the max degree allocated
		innerloop : for (int j = 0; j < edgesToAdd; j++) {
			int node1 = 1 << rng.nextInt(nC);
			int node2 = 1 << rng.nextInt(nC);

			BitVector bv = new BitVector(node1 + node2);

			int attempts = 0;
			while (newbie.isBadEdge(bv) && attempts < 15) {
				node1 = 1 << rng.nextInt(nC);
				node2 = 1 << rng.nextInt(nC);
				bv = new BitVector(node1 + node2);
				attempts++;
			}

			if (attempts < 15) {
				newbie.addEdge(bv);
			} else {
				break innerloop;
			}
		}
		return newbie;
	}
	
	/**
	 * Repeatedly try to create a graph randomly that matches the required cell. Loops
	 * iteration times
	 * @param cell
	 * @param iterations
	 * @return
	 */
	public static ArrayList<Graph> findGraphForCellRandomly(Cell cell, int iterations){
		ArrayList<Graph> graphs = new ArrayList<Graph>();
		int rank = cell.getNumPorts();

		for (int i = 0; i < iterations; i++) {
			Graph newbie = tryToCreateGraph(rank);

			// test what cell this graph is
			Cell gsCell = GraphtoCell.makeCell(newbie);
			if (gsCell.size() != 0) {
				gsCell.normalize();
				gsCell.sortBySize();
				if (gsCell.equals(cell)) {
					newbie.tryToFixCycleSize();
					if (newbie.tryToConnect() && newbie.isRealistic()) {
						graphs.add(newbie);
					}
				}
			}
		}
		
		return graphs;
	}
	
	/**
	 * Repeatedly try to create random graphs, and test whether they match any cell in an entire rank. Returns
	 * all matchings
	 * @param classifications
	 * @return
	 */
	public static ArrayList<ArrayList<Graph>> findGraphForAllCellsRandomly(ArrayList<Cell> classifications) {
		ArrayList<ArrayList<Graph>> graphs = new ArrayList<ArrayList<Graph>>();
		for (int i = 0; i < 214; i++) {
			graphs.add(new ArrayList<Graph>());
		}

		int rank = classifications.get(0).getNumPorts();
		// only try 1000 times
		for (int i = 0; i < 100000; i++) {
			if (i % 1000 == 0) {
				System.out.println(i);
			}
			Graph newbie = tryToCreateGraph(rank);

			// test what cell this graph is
			Cell gsCell = GraphtoCell.makeCell(newbie);
			if (gsCell.size() != 0) {
				gsCell.normalize();
				gsCell.sortBySize();
				int index = classifications.indexOf(gsCell);
				// found matching cell
				if (index != -1) {
					newbie.tryToFixCycleSize();
					if (newbie.tryToConnect() && newbie.isRealistic()) {
						graphs.get(index).add(newbie);
					}
				}
			}
		}
		return graphs;
	}

}
