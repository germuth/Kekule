package makeCell;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import shared.BitVector;

/**
 * Utils
 * 
 * Utility class used to hold helper functions for the rest of the program
 * @author Aaron
 *
 */
public class Utils {
	
	/**
	 * Sorts a string of characters
	 * For example, 
	 * 
	 * dca -> acd
	 * 
	 * @param unsorted String
	 * @return, sorted String
	 */
	public static String sort(String unsorted){
		char[] chars = unsorted.toCharArray();
		Arrays.sort(chars);
		return new String(chars);
	}
	
	/**
	 * Translates a set of BitVectors over a BitVector. Alternate method to translate in cell, which 
	 * translates all port assignments of that cell over the bitVector. This can be used seperately from
	 * any cell Objects.
	 * @param bitvector we want to translate over
	 */
	public static Set<BitVector> translate(Set<BitVector> set, BitVector translation){
		//if nothing to translate
		if(translation.isEmpty()){
			return set;
		}
		//new set of translated port assignments
		Set<BitVector> translated = new HashSet<BitVector>();
		
		//iterate over all cells current port assignments
		Iterator<BitVector> i = set.iterator();
		while(i.hasNext()){
			BitVector bv = (BitVector)i.next();
			//get symmetric difference of current and translation
			BitVector symD = BitVector.symmetricDifference(
					bv,
					translation);
			//place intersection in new port assignment set
			translated.add( symD );
		}
		
		return translated;
	}
	
	/**
	 * Converts an array of BitVectors to a Set of BitVectors. 
	 * Careful: Order is lost
	 * @param ar, array to copy
	 * @return set, containing all elements the array had
	 */
	public static Set<BitVector> arToSet(BitVector[] ar){
		Set<BitVector> bvSet = new HashSet<BitVector>();
		for(BitVector bv: ar){
			bvSet.add(bv);
		}
		return bvSet;
	}
	
	/**
	 * Takes the union of two Sets of BitVectors. Returns a new Set containing
	 * all of elements of both sets
	 * @param a, first Bit vector set
	 * @param b, second BitVector set
	 * @return their union
	 */
	public static Set<BitVector> union(Set<BitVector> a, Set<BitVector> b){
		Set<BitVector> union = new HashSet<BitVector>();
		union.addAll(a);
		union.addAll(b);
		return union;
	}
}
