package bitvectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Permutations {
	
	private static final int MAX_RANK = 11;
	
	private static boolean permInitialized;
	private static ArrayList<Cell[]> perms = new ArrayList<Cell[]>();
	
	public static Cell firstVariant(Cell cell) {
		//number of the ports
		int rank = cell.getNumPorts();
		Cell pe = somePHDVariant(rank, cell);
		ArrayList<Cell> array = rawPermVariants(rank, pe);
		
		return pe;
	}
	
	/**
	 * 
	 */
	private static ArrayList<Cell> rawPermVariants(int rank, Cell cell){
		int[] h = new int[11];
		Histogram.portHisto(cell, h);
		ArrayList<Cell> perms = specPerm(rank, h);
		
		return null;
	}

	/**
	 * Perm must be initialized, and hist[0 -> rank - 1] must
	 * be descending
	 * 
	 * creates a list of permutations as powers of 2 which 
	 * preserve hist 0 -> rank -1
	 */
	private static ArrayList<Cell> specPerm(int rank, int[] hist){
		
		ArrayList<Cell> allPermutations = new ArrayList<Cell>();
		ArrayList<Cell> locperm;
		
		Set<BitVector> prims = new HashSet<BitVector>();
		for(int i = 0, x = 1; i < rank; i++, x <<= 1 ){
			prims.add(new BitVector(x));
		}
		allPermutations.add(new Cell(prims, 0));
		int i = 0;
		while( i + 1 < rank){
			int j = i + 1;
			while(j < rank && hist[i] == hist[j]){
				j++;
			}
			if(i+1 < j){
				int x = allPermutations.size();
				locPerm = getPerm(j-i);
				
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 */
	private static void initializePerm(){
		Permutations.permInitialized = true;

		Permutations.perms[0] = new ArrayList<Cell>();
		Permutations.perms[1] = new ArrayList<Cell>();
		Cell p0 = null;
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
		for(int i = 0; i < rank * perms[rank-1].size(); i++){
			ArrayList<Cell> y = perms[rank-1];
			for(int j = 0; j < rank; j++){
				ArrayList<Cell> z = new ArrayList<Cell>();
				for(int k = 0; k < j; k++){
					
				}
			}
		}
		
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
		int[] hh = new int[10];
		Cell pe = below(rank); 
		Cell pg = null;
		Histogram.rank = rank;
		Histogram.portHisto(cell, hh);
		
		//sort hh and pe
		//check this...
		Arrays.sort(hh);
		pe.sortBySize();
		
		BitVector[] pgBV = new BitVector[pe.size()];
		int p = 1;
		for(int i = 0; i < rank; i++){
			int num = pe.getPortAssignments()[i].getNumber();
			pgBV[ pe.getPortAssignments()[i].getNumber() ] = new BitVector(p);
			p <<= 1;
		}
		Set<BitVector> pgSet = new HashSet<BitVector>();
		for(int i = 0; i < pgBV.length; i++){
			pgSet.add(pgBV[i]);
		}
		pg = new Cell(pgSet, 0);
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
			BitVector current = cell.getPortAssignments()[i];
			int x = current.getNumber();
			int y = 0;
			int j = 0;
			while ( x > 0 ){
				BitVector perm = permutation.getPortAssignments()[j];
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
