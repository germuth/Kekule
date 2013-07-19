package classification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


import shared.BitVector;
import shared.Cell; 
import shared.Histogram;
import shared.Permutations;
import shared.Utils;

/**
 * Classify
 * 
 * This class composes all possible Kekule cells of a given rank (number of ports). It then reduces
 * all possible states by normalization. This includes translating the cell to include 0 and centering it.
 * Once normalized, there are only 24 distinct Kekule cells of 5 ports. The classification steps are based on
 * those outlined in Hesselink's Paper.
 * @author Aaron
 *
 */
public class Classify {

	public static void main(String[] args) {
		
		//TODO USER ACCESSIBLE
		//PARAMETERS
		int rank = 5;
		int option = 0;
		
		ArrayList<Cell> classifications = null;
		
		//TODO MAKE OPTION 2
		//if option 2, raw classification is provided and not needed to be calculated
		if(option == 2){
			//read Cell list
			//setRank
		}
		//create raw classification
		else{
			classifications = classify(rank, option);
		}
		//trim raw classification to final result
		if(option != 1){
			classifications = sortAndWeed(rank, classifications);
			Permutations.freePerm();
			System.out.println("Classification of Rank " + rank + ":");
			System.out.println(classifications.size() + " classes in Total");
			System.out.println("");
			for(int i= 0; i < classifications.size(); i++){
				System.out.println("K" + (i + 1) + " " + classifications.get(i));
				System.out.println("\t" + classifications.get(i).printNumbers());
			}
		}
	}
	
	/**
	 * Returns the raw classification of all Kekule cells with port number = rank
	 * 
	 * @param rank, number of ports we are classifying
	 * @param option,
	 * 0 = raw and trimmed
	 * 1 = raw
	 * 2 = trimmed
	 * @return, ArrayList of Cells, each containing one class of Kekule cell
	 */
	public static ArrayList<Cell> classify(int rank, int option){
		
		ArrayList<Cell> answer = new ArrayList<Cell>();
		
		//Port Vector
		BitVector ports = new BitVector( ( 1 << rank ) - 1);
		//All port assignments of even length
		Cell even = Cell.allEvenPA(rank);
		
		//create cand, and add 0
		Set<BitVector> candSet = new HashSet<BitVector>();
		candSet.add(new BitVector(0));		
		Cell cand = new Cell(candSet, rank);
		
		Histogram.rank = rank;
		//represents bit vector of the even set
		//each 1 in bitvector represents the set is present
		//if even = 0 ab ac bc
		//bit vector = 1 1 1 1 or 15
		//if  subset = 0 ac
		//bit vector = 1 0 1 0 or 10 
		long ulimit = (long)Math.pow(2, even.size() - 1);

		int number = 1;
		//loop through once for every possible subset of even
		for(long x = 0; x < ulimit; x++){
			Set<BitVector> tempSet = new HashSet<BitVector>();
			tempSet.add(cand.getPA()[0]);
			cand = new Cell(tempSet, rank);
			
			//only add if they don't divide evenly into 1
			//which means they are odd
			//odd numbers in binary always will have a 1
			//in the least significant bit
			//this means all subsets which contain the first element
			//first element corresponds to 0
			//which makes sense because all normalized cells must be centered
            long y = x, i = 1;
			while( y > 0 ){
				if (y % 2 == 1) {
					cand.add(even.getPA()[(int) i]);
				}
				y /= 2;
				i++;
			}
			
			//If cell meets all requirements, add to raw classification
			//Flexible: All ports are contained in at least one port assignment
			//	ex. If there are 4 ports, you should be able to see a, b, c, and d
			//	in atleast one port assignment
			//PortHistogramDescending: Is the weighted port histogram of this cell
			//	descending. A descending weighted histogram represents that the 
			//	most often occurring ports are concentrated at the beginning of the
			//	port enumeration
			//Centered: The center of a cell K is deemed the set of elements with 
			//	the smallest h values. A cell is said to be centered if the port
			//	assignment '0' is within the center.
			//Coherent: TODO
			//	whether all ports of the cell are connected by channels
			if (cand.isFlexible(ports.getNumber())
					&& Histogram.isPortHistoDescending(cand)
					&& Histogram.isCentered(cand) && Coherence.isCoherent(cand)) {
				if (option != 0) {
					// printCell()
				} else {
					cand.sortBySize();
					answer.add(new Cell(cand));
				}
			}

		}
		return answer;
	}

	/**
	 * Takes in an array of Kekule cells representing the raw classification for
	 * one rank of Kekule cells. It then trims this list by merging Kekule cells
	 * which are deemed equivalent by Hesselink's standards.
	 * @param rank, the number of ports involved in the classification
	 * @param array, the array of raw classification
	 * @return the trimmed array
	 */
	public static ArrayList<Cell> sortAndWeed(int rank, ArrayList<Cell> array){
		//Sorts cells of the raw classification based on their weighted histogram 
		//length. In the case of equality, their normal length is used
		//smaller cells are sorted to the beginning of the list
		Cell[] tobeSorted = Utils.listToArCell(array);
		Arrays.sort(tobeSorted, new Comparator<Cell>(){

			@Override
			public int compare(Cell cell1, Cell cell2) {
				int r = Histogram.compareW(cell1, cell2);
				if(r != 0){
					return r;
				}
				return Histogram.compareL(cell1, cell2);
			}
			
		});
		array = Utils.arToList(tobeSorted);
		
		
		ArrayList<Cell> variants;
		
		//loop through raw classification
		int i = 0;
		while( i < array.size() ){
			
			int j = i + 1;
			//loop until j not null
			while( j < array.size() && array.get(j) == null ){
				j++;
			}
			
			//if j reaches end but i and j have different weighted sort lengths
			if( j == array.size() || ( Histogram.compareW( array.get(i), array.get(j) ) != 0 ) ){
				i = j;
			}
			//not at end
			else{
				//get all possible variants of the current rank based on cell i 
				variants = Permutations.allVariants(rank, array.get(i) );
				
				//for each variant of variants, if the variant and j have the same amount of port assignments
				//j is removed from the classification
				int k = 1;
				while( k < variants.size() && j < array.size() ){
					if( array.get(j) != null && Histogram.compareL(array.get(j), variants.get(k) ) == 0){
						array.set(j, null);
						k++;
					}
					j++;
				}
				
				//loop until i is not null
				i++;
				while( i < array.size() && array.get(i) == null){
					i++;
				}
			}
		}
		//remove all null entries
		array = Utils.removeNulls(array);
		//final classification is complete
		return array;
	}
	
	/**
	 * Removes uncessary cells from raw
	 * TODO seems to be the excat same as above...
	 * 		except permVariants instead of allVariants
	 * @param rank
	 * @param raw
	 * @return
	 */
	public static ArrayList<Cell> sortAndWeedGraphs(int rank, ArrayList<Cell> raw){
		Cell[] tobeSorted = Utils.listToArCell(raw);
		Arrays.sort(tobeSorted, new Comparator<Cell>(){

			@Override
			public int compare(Cell cell1, Cell cell2) {
				int r = Histogram.compareW(cell1, cell2);
				if(r != 0){
					return r;
				}
				return Histogram.compareL(cell1, cell2);
			}
			
		});
		
		raw = Utils.arToList(tobeSorted);
		ArrayList<Cell> variants = null;
		
		int i = 0;
		while( i < raw.size() ){
			int j = i + 1;
			while( j < raw.size() && raw.get(j) == null ){
				j++;
			}
			if( j == raw.size() || ( Histogram.compareW( raw.get(i), raw.get(j) ) != 0 ) ){
				i = j;
			}
			else{
				variants = Permutations.permVariants(rank, raw.get(i) );
				int k = 1;
				while( k < variants.size() && j < raw.size() ){
					if( raw.get(j) != null && 
							Histogram.compareL(raw.get(j), variants.get(k) ) == 0){
						raw.set(j, null);
						k++;
					}
					j++;
				}
				i++;
				while( i < raw.size() && raw.get(i) == null){
					i++;
				}
			}
		}
		raw = Utils.removeNulls(raw);
		return raw;
	}
}
