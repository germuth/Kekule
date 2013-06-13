package shared;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


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
	 * Converts an array to an array list
	 */
	public static ArrayList<Cell> arToList(Cell[] param){
		ArrayList<Cell> answer = new ArrayList<Cell>();
		for(int i=  0; i < param.length; i++){
			answer.add(param[i]);
		}
		return answer;
	}
	
	/**
	 * Converts list to array
	 */
	public static Cell[] listToArCell(ArrayList<Cell> arr){
		Cell[] newOne = new Cell[arr.size()];
		for(int i = 0; i < arr.size(); i++){
			newOne[i] = arr.get(i);
		}
		return newOne;
	}
	
	public static BitVector[] listToArBV(ArrayList<BitVector> arr){
		BitVector[] newOne= new BitVector[arr.size()];
		for(int i = 0; i < arr.size(); i++){
			newOne[i] = arr.get(i);
		}
		return newOne;
	}
	
	public static void printArrayList(ArrayList<Cell> variants){
		for(int p = 0; p < variants.size(); p++){
			System.out.println(variants.get(p).printNumbers());
		}
	}
	
	/**
	 * Tests whether item is in the list
	 */
	public static boolean isMember(Cell cell, ArrayList<Cell> list){
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).equals(cell)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Delete all duplicates in arraylist by turning to set and then back
	 */
	public static ArrayList<Cell> deleteDuplicates(ArrayList<Cell> cell){
		ArrayList<Cell> newList = new ArrayList<Cell>();
		newList.add(cell.get(0));
		for(int i = 0; i < cell.size(); i++){
			
			boolean found = false;
			
			inner:
			for(int j = 0; j < newList.size(); j++){
				if(newList.get(j).equalsNoPorts(cell.get(i))){
					found = true;
					break inner;
				}
			}
			if(!found){
				newList.add(cell.get(i));
			}
		}
	
		return newList;
	}
	
	/**
	 * Removes all null entries from array list
	 */
	public static ArrayList<Cell> removeNulls(ArrayList<Cell> array){
		ArrayList<Cell> newList = new ArrayList<Cell>();
		for(int i = 0; i < array.size(); i++){
			if(array.get(i) != null){
				newList.add(array.get(i));
			}
		}
		return newList;
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
