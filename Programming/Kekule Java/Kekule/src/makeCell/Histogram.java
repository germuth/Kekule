package makeCell;

import java.util.HashSet;
import java.util.Set;

import shared.BitVector;
import shared.Cell;

/**
 * Histogram 
 * 
 * Don't understand fully yet
 * @author Aaron
 *
 */
public class Histogram {
	public static int rank;

	public static int compareL(Cell one, Cell two) {

		int diff = one.getPA().length - two.getPA().length;
		if (diff != 0) {
			return diff;
		}

		return compareLex(one.getPA().length, one, two);
	}

	public static int compareLex(int diff, Cell one, Cell two) {
		diff--;
		while (diff >= 0
				&& one.getPA()[diff].equals(two.getPA()[diff])) {
			diff--;
		}
		if (diff < 0) {
			return 0;
		}
		return one.getPA()[diff].getNumber()
				- two.getPA()[diff].getNumber();
	}

	// i think hist is always intialized as 0
	// and then it fills it out based on cell
	public static void portHisto(Cell cell, Integer[] hist) {
		for (int i = 0; i < rank; i++) {
			// WOAH this array might be too big
			hist[i] = 0;
		}
		for (int j = 0; j < cell.size(); j++) {
			BitVector current = cell.getPA()[j];
			int k = current.getNumber();
			// if rank 5
			// w:= gives 103
			// w:= cntBits gives 38
			//w:= cntBits +1 only gives 32!
			int w = current.countBits() + 1;
			int i = 0;
			while(k > 0){
				if(k % 2 != 0){
					hist[i] += w;
				}
				k /= 2;
				i++;
			}
		}
	}
	
	public static boolean isPortHistoDescending(Cell cell){
		//is the weighted port histogram descending
		Integer hist[] = new Integer[Permutations.MAX_RANK];
		portHisto(cell, hist);
		int k = hist[0];
		for(int i = 0; i < rank; i++){
			int m = hist[i];
			if( k < m ){
				return false;
			}
			k = m;
		}
		return true;
	}
	
	public static boolean isCentered(Cell cell){
		Cell owl;
		Cell other = new Cell();
		
		cell.sortBySize();
		//if cell has no element or first element isn't zero
		//make sure it's sorted
		if(cell.size() == 0 || cell.getPA()[0].getNumber() > 0){
			return false;
		}
		owl = new Cell(cell);
		owl.weightedSort();
		
		int r = 0;
		for(int i = 0; r <= 0 && i < cell.size(); i++){
			for(int j = 0; j < cell.size(); j++){
				BitVector symDifference = BitVector.symmetricDifference(
								cell.getPA()[i], cell.getPA()[j] );
				other.add(symDifference);
			}
			other.weightedSort();
			r = compareL(owl, other);
		}
		return ( r <= 0 );
	}
	
}
