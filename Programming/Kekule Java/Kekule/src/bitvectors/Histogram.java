package bitvectors;
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
	
}
