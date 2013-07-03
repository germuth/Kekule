package classification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import shared.BitVector;
import shared.Cell;

/**
 * Coherence
 * 
 * This class holds many utility functions involved with Cell coherence and
 * Channels. It also holds a static variable, chFrom which holds 
 * some discovered channels so they don't have to be recalculated.
 * 
 * @author Aaron
 *
 */

public class Coherence {
	
	private static Cell[] chFrom = new Cell[32];

	/**
	 *  //TODO make sure this is right
	 * Returns whether all nodes of the cell contain a channel to one another
	 * @param cell
	 * @return
	 */
	public static boolean isCoherent(Cell cell){
		
		if(cell.size() < 2){
			return true;
		}
		
		int b = 1;
		for(int p = 0; (b != 0) && p < cell.size(); p++){
			for(int q = p + 1; (b != 0) && q < cell.size(); q++){
				b = channelConnect(p, q, cell);
			}
		}
		for(int p = 0; p < cell.size(); p++){
			chFrom[p] = null;
		}
		if(b == 1){
			return true;
		}
		return false;
	}
	
	//pre p < q < cell.size() and distance > 2
	/**
	 * Returns whether there exists a channel between node p and node q
	 * within Cell cell. 
	 * @param p, the first node (integer form)
	 * @param q, the second node (integer form)
	 * @param cell, the cell we are checking whether it contains a channel from
	 * p to q
	 * @return, whether a channel from p to q exists within cell
	 */
	private static int channelConnect(int p, int q, Cell cell){
		BitVector pp = cell.getPA()[p];
		BitVector symDiff = BitVector.symmetricDifference(
				pp, cell.getPA()[q] );
		int count = symDiff.countBits();
		if(count <= 2){
			return 1;
		}
		Cell chs1 = Cell.intersection(chansFrom(p, cell), chansFrom(q,cell));
		chs1.filterSub(symDiff);
		
		if( 2 * chs1.size() < count ){
			return 0;
		}
		
		ArrayList<Cell> li = new ArrayList<Cell>();
		li = chanDivide(symDiff, chs1);
		//if there are never more than 3 disjoint channels 
		// rank < 8
		//isfan holds because of the constructor of chs1
		//Therefore the netx loop is superfluouse we only need li
		//non empty
		
		int i = 0;
		
		while( i < li.size() && !isfan(pp, li.get(i), cell)){
			i++;
		}
		if( i < li.size() ){
			return 1;
		}
		return 0;
	}
	
	/**
	 *pre: p in cell. Preserves both cell and fan
	 * for every subset D of fan, verify that p^(^D) in cell
	 * Determines for every subset of the cell fan, that the intersection of
	 * a bit vector p and above subset is within cell. This means that fan
	 * is a fan of cell
	 * @param p, Bit Vector p
	 * @param fan, possible fan cell
	 * @param cell, cell we are checking to see if has fan
	 * @return, whether fan is a fan of cell
	 */
	private static boolean isfan(BitVector p, Cell fan, Cell cell){
		if(fan.size() == 0){
			return true;
		}
		BitVector x = fan.removeLast();
		BitVector symDiffPX = BitVector.symmetricDifference( p, x );
		boolean b =      isfan(p, fan, cell) &&
				         symDiffPX.isMember(cell) && 
				         isfan(symDiffPX, fan, cell);
		//fan.size++
		return b;
	}
	
	/**
	 * Returns whether given BitVector is a channel. It is a channel
	 * if there are only two nodes in the bitvector, and one of them
	 * is 1
	 * TODO check correctness
	 * @param bv
	 * @return
	 */
	private static boolean isChan(BitVector bv){
		int count = 0;
		int x = bv.getNumber();
		while( x > 0 && count < 3){
			count += x % 2;
			x /= 2;
		}
		return (count == 2);
	}
	
	/**
	 * All elements of set are subsets of bit string k
	 * give all wasy to write bit string k as a disjoint union of difference elements of set
	 * destroys the set
	 * TODO what do?
	 * @param difference
	 * @param cell
	 * @return
	 */
	private static ArrayList<Cell> chanDivide(BitVector difference, Cell cell){
		ArrayList<Cell> answer = new ArrayList<Cell>();
		if(cell.size() == 0){
			if(difference.isEmpty()){
				answer.add(new Cell());
			}
			return answer;
		}
		BitVector ch = cell.removeLast();
		Cell s2 = new Cell(cell);
		//ch not used here
		answer = chanDivide(difference, s2);
		difference = BitVector.symmetricDifference(
				difference, ch);
		cell.filterSub(difference);
		
		ArrayList<Cell> array = new ArrayList<Cell>();
		array = chanDivide(difference, cell);
		
		for(int i = 0; i < array.size(); i++){
			Cell current = array.get(i);
			current.add(difference);
		}
		
		//add everything from array to answer
		for(int i= 0; i < array.size(); i++){
			answer.add(array.get(i));
		}
		return answer;
	}
	
	/**
	 * Returns a cell containing the sorted list of channels c where 
	 * cell[p] ^ c is in the cell. 
	 * 
	 * cell[p] = cell.getPA()[p]
	 * ^ = intersection of cell[p] and channel
	 * @param p, integer representing index of cell we are trying to find channels for
	 * @param cell, the cell we are looking for channels in
	 * @return, list of channels which satisfy above conidition
	 */
	private static Cell chansFrom(int p, Cell cell){
		//if already found
		if( chFrom[p] != null ){
			return chFrom[p];
		}
		
		Set<BitVector> set = new HashSet<BitVector>();
		for(int i = 0; i < cell.size(); i++){
			BitVector symDiff = BitVector.symmetricDifference(
					cell.getPA()[p],  cell.getPA()[i] );
			if( isChan(symDiff) ){
				set.add(symDiff);
			}
		}
		Cell answer = new Cell(set, 0);
		answer.sortBySize();
		return answer;
	}

}
