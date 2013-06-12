package bitvectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
/**
 * Permutations 
 * 
 * Don't understand fully yet
 * @author Aaron
 *
 */
public class Permutations {
	/**
	 * Represents the max rank which is allowed. Graphs with
	 * more than 12 ports are not valid.
	 */
	public static final int MAX_RANK = 11;
	
	private static boolean permInitialized;
	private static ArrayList<Cell>[] perms = new ArrayList[MAX_RANK];
	
	public static Cell firstVariant(Cell cell) {
		//number of the ports
		int rank = cell.getNumPorts();
		Cell pe = somePHDVariant(rank, cell);
		ArrayList<Cell> array = rawPermVariants(rank, pe);
		pe = array.get(0);
		for(int i = 1; i < array.size(); i++){
			if( Histogram.compareL(array.get(i), pe) < 0 ){
				pe = array.get(i);
			}
		}
		return pe;
	}
	
	/**
	 * 
	 */
	private static ArrayList<Cell> rawPermVariants(int rank, Cell cell){
		Integer[] h = new Integer[Permutations.MAX_RANK];
		Histogram.portHisto(cell, h);
		ArrayList<Cell> perms = specPerm(rank, h);
		ArrayList<Cell> answer = new ArrayList<Cell>();
		for(int i = 0; i < perms.size(); i++){
			answer.add( permuteCell(cell, perms.get(i)) );
		}
		perms = null;
		return answer;
	}

	/**
	 * Perm must be initialized, and hist[0 -> rank - 1] must
	 * be descending
	 * 
	 * creates a list of permutations as powers of 2 which 
	 * preserve hist 0 -> rank -1
	 */
	private static ArrayList<Cell> specPerm(int rank, Integer[] hist){
		
		Cell prim;
		ArrayList<Cell> locperm;
		ArrayList<Cell> answer = new ArrayList<Cell>();
		
		BitVector[] prims = new BitVector[rank];
		for(int i = 0, x = 1; i < rank; i++, x <<= 1 ){
			prims[i] = new BitVector(x);
		}
		prim = new Cell(prims);
		answer.add(prim);
		
		int i = 0;
		while( i + 1 < rank){
			int j = i + 1;
			while(j < rank && hist[i] == hist[j]){
				j++;
			}
			if(i+1 < j){
				int x = answer.size();
				locperm = getPerm(j-i);
				for(int k = 0; k < locperm.size() - 1; k++){
					for(int m = 0; m < x; m++){
						answer.add(applyPerm(i, answer.get(m), locperm.get(k)));
					}
				}
			}
			i = j;
		}
		
		return answer;
	}
	
	private static Cell applyPerm(int shift, Cell arg, Cell pm) {
		Cell answer = new Cell(arg);
		for(int i = 0; i < pm.size(); i++){
			answer.getPA()[i+shift] = arg.getPA()[pm.getPA()[i].getNumber() + shift];
		}
		return answer;
	}

	/**
	 * 
	 */
	private static void initializePerm(){
		Permutations.permInitialized = true;

		Permutations.perms[0] = new ArrayList<Cell>();
		Permutations.perms[1] = new ArrayList<Cell>();
		//not sure
		Cell p0 = new Cell(new BitVector[0]);
		perms[0].add(p0);
		
		Set<BitVector> temp = new HashSet<BitVector>();
		temp.add(new BitVector(0));
		perms[1].add(new Cell(temp, 0));
		
	}
	
	/**
	 * 
	 */
	private static ArrayList<Cell> getPerm(int rank){
		if(!permInitialized){
			initializePerm();
		}
		if(perms[rank] != null){
			return perms[rank];
		}
		if(perms[rank-1] == null){
			getPerm(rank-1);
		}
		perms[rank] = new ArrayList<Cell>();
		for(int i = 0; i < perms[rank-1].size(); i++){
			Cell y = perms[rank-1].get(i);
			for(int j = 0; j < rank; j++){
				Cell z;
				BitVector[] zSet = new BitVector[rank];
				
				for(int k = 0; k < j; k++){
					zSet[k] = (y.getPA()[k]);
				}
				z = new Cell(zSet);
				z.getPA()[j] = new BitVector(rank - 1);
				
				for(int k = j; k < y.size(); k++){
					z.getPA()[k+1] = y.getPA()[k];
				}
				perms[rank].add(z);
			}
		}
		return perms[rank];
		
	}
	
	/**
	 * Cell must be centered
	 * Permutes cell to become port Hist Descending PHD
	 * destroys cell but returns new one to replace it
	 * @param rank
	 * @param cell
	 * @return
	 */
	private static Cell somePHDVariant(int rank, Cell cell) {
		Integer[] hh = new Integer[MAX_RANK];
		for(int i = 0; i < hh.length; i++){
			hh[i] = 0;
		}
		Cell pe = below(rank); 
		Cell pg = null;
		Histogram.rank = rank;
		Histogram.portHisto(cell, hh);
		
		/* insertion sort of hh, invariant: hh[0..i) is sorted */
		int i = 1;
		int j = 0;
		int p, q = 0;
		while (i < rank) {
			j = i;
			p = hh[i];
			q = pe.getPA()[i].getNumber();
			i++;
			while (j > 0 && hh[j-1] < p) {
				hh[j] = hh[j-1];
				pe.getPA()[j] = pe.getPA()[j-1];
				j--;
			}
			hh[j] = p;
			pe.getPA()[j] = new BitVector(q);
		}
		
		BitVector[] pgBV = new BitVector[pe.size()];
		p = 1;
		for(i = 0; i < rank; i++){
			int num = pe.getPA()[i].getNumber();
			pgBV[ num ] = new BitVector(p);
			p <<= 1;
		}
		pg = new Cell(pgBV);
		pe = permuteCell(cell, pg);
		pg = null;
		cell = null;
		return pe;
	}
	
	/**
	 * Must be a permutation of powers of 2
	 * returns the port permutation of inputted cell
	 */
	private static Cell permuteCell(Cell cell, Cell permutation){
		Set<BitVector> result = new HashSet<BitVector>();
		
		for(int i = 0; i < cell.size(); i++){
			BitVector current = cell.getPA()[i];
			int x = current.getNumber();
			int y = 0;
			int j = 0;
			while ( x > 0 ){
				BitVector perm = permutation.getPA()[j];
				y += ( x % 2 ) * perm.getNumber();
				x /= 2;
				j++;
			}
			result.add(new BitVector(y));
		}
		Cell answer = new Cell(result, 0);
		//check this
		answer.sortBySize();
		
		return answer;
	}
	
	/**
	 * makes a cell with bitvectors 0 - n-1
	 * @param n
	 * @return
	 */
	private static Cell below(int n){
		Set<BitVector> set = new HashSet<BitVector>();
		for(int i = 0; i < n; i++){
			set.add(new BitVector(i));
		}
		return new Cell(set, 0);
	}

}
