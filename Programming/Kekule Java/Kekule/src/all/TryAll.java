package all;

import java.io.File;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import makeCell.GraphtoCell;
import shared.BitVector;
import shared.Cell;
import shared.Graph;

/**
 * @param args
 */
// skip over title
public class TryAll {
	private static File f = new File("myraw.txt");
	private static Scanner fileScanner;
	private static int rank;
	private static String name;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Cell input = null;
		try {
			fileScanner = new Scanner(f);
			readTitle();
			input = readCell();
		} catch (Exception e) {
			System.out.println("error reading from file");
			e.printStackTrace();
			System.exit(0);
		}
		int i = 1;
		while (input != null) {
			int numNodes = rank;
			while (numNodes < 8) {
				System.out.println(numNodes + " nodes.");
				Set<BitVector> allPossibleEdges = allEdges(numNodes);

				PowerSet<BitVector> powerset = new PowerSet<BitVector>(
						allPossibleEdges);

				Iterator<Set<BitVector>> graphsI = powerset.iterator();
				while( graphsI.hasNext() ){
					Set<BitVector> set = graphsI.next();
					
					if(set == null){
						continue;
					}
					
					Graph g = new Graph(rank, numNodes, new Cell(set, rank));
					Cell c = GraphtoCell.makeCell(g);
					if ( c.equalsNoPorts(input) && g.getHighestDegree() < 4 && g.getHighestPortDegree() < 3) {
						g.setName(name);
						g.writeGraph();
					}
				}
				numNodes++;
			}
			try {
				input = readCell();
				i++;
				System.out.println("----- K" + i + " -----");
			} catch (Exception e) {
				input = null;
			}
		}
	}

	private static Set<BitVector> allEdges(int numNodes) {
		Set<BitVector> allEdges = new HashSet<BitVector>();
		int finalNode = 1 << (numNodes - 1);

		for (int node1 = 1; node1 <= finalNode; node1 *= 2) {

			for (int node2 = 2 * node1; node2 <= finalNode; node2 *= 2) {
				allEdges.add(new BitVector(node1 + node2));
			}
		}

		return allEdges;
	}

	public static void readTitle() throws Exception {
		String title = fileScanner.nextLine();
		String[] words = title.split(" ");
		String ranks = words[words.length - 1];
		ranks = ranks.substring(0, ranks.length() - 1);
		rank = Integer.parseInt(ranks);
	}

	public static Cell readCell() throws Exception {

		String cell = fileScanner.nextLine();
		// skip over title
		while (!cell.contains("K")) {
			cell = fileScanner.nextLine();
		}

		Scanner lineScanner = new Scanner(cell);
		name = lineScanner.next();

		String bitVectors = fileScanner.nextLine();

		lineScanner.close();
		lineScanner = new Scanner(bitVectors);

		Set<BitVector> allBVs = new HashSet<BitVector>();

		while (lineScanner.hasNext()) {
			String bitvector = lineScanner.next();
			bitvector = bitvector.trim();
			int number = Integer.parseInt(bitvector);
			BitVector bV = new BitVector(number);
			allBVs.add(bV);
		}

		Cell input = new Cell(allBVs, rank);
		lineScanner.close();

		return input;
	}
}

/**
 * @author st0le
 * 
 */
class PowerSet<E> implements Iterator<Set<E>>, Iterable<Set<E>> {
	private E[] arr = null;
	private BitSet bset = null;

	@SuppressWarnings("unchecked")
	public PowerSet(Set<E> set) {
		arr = (E[]) set.toArray();
		bset = new BitSet(arr.length + 1);
	}

	@Override
	public boolean hasNext() {
		return !bset.get(arr.length);
	}

	@Override
	public Set<E> next() {
		Set<E> returnSet = new TreeSet<E>();
		for (int i = 0; i < arr.length; i++) {
			if (bset.get(i))
				returnSet.add(arr[i]);
		}
		// increment bset
		for (int i = 0; i < bset.size(); i++) {
			if (!bset.get(i)) {
				bset.set(i);
				break;
			} else
				bset.clear(i);
		}
		
		int size = returnSet.size();
		if(size != 7){//if( size > 8 || size < 7){//if( size < 4 || size > 8){
			return null;
		}

		return returnSet;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Not Supported!");
	}

	@Override
	public Iterator<Set<E>> iterator() {
		return this;
	}
}
