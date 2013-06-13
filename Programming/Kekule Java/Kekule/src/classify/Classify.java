package classify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import makeCell.Histogram;

import shared.BitVector;
import shared.Cell; 
import shared.Permutations;
import shared.Utils;

public class Classify {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ArrayList<Cell> classifications = null;
		int rank = 5;
		int option = 0;
		
		if(option == 2){
			//read Cell list
			//setRank
		}
		else{
			classifications = classify(rank, option);
			System.out.println(classifications.size());
			for(int i = 0; i < classifications.size(); i++){
				System.out.println(classifications.get(i).printNumbers());
			}
		}
		System.out.println(" ");
		if(option != 1){
			sortAndWeed(rank, classifications);
			Permutations.freePerm();
			for(int i= 0; i < classifications.size(); i++){
				System.out.println(classifications.get(i).printNumbers());
			}
		}
	}
	
	public static ArrayList<Cell> classify(int rank, int option){
		
		ArrayList<Cell> answer = new ArrayList<Cell>();
		
		BitVector ports = new BitVector( ( 1 << rank ) - 1);
		Cell even = Cell.allEvenPA(rank);
		
		Set<BitVector> candSet = new HashSet<BitVector>();
		candSet.add(new BitVector(0));
		
		long ulimit = (1 << ( even.size() - 1)); 
		
		Cell cand = new Cell(candSet, rank);
		Histogram.rank = rank;
		
		for(long x = 0; x < ulimit; x++){
			Set<BitVector> tempSet = new HashSet<BitVector>();
			tempSet.add(cand.getPA()[0]);
			cand = new Cell(tempSet, rank);
			
			long y = x;
			int i = 1;
			while( y > 0 ){
				if (y % 2 == 1) {
					cand.add(even.getPA()[i]);
				}
				y /= 2;
				i++;
			}
			// flexible is 9846 instead of about 10320
			// Histogram is 1012 to 1012 i'm pretty sure

			if(x == 7927){
				System.out.println("");
			}
			
			if (cand.isFlexible(ports.getNumber())
					&& Histogram.isPortHistoDescending(cand)
					&& Histogram.isCentered(cand) && Coherence.isCoherent(cand)) {
				if (option != 0) {
					// printCell()
				} else {
					cand.sortBySize();
					if (cand.printNumbers().contains("0 3 5 6 10 12 15 17")) {
						System.out.println(cand.printNumbers());
						System.out.println(x);
					}
					answer.add(new Cell(cand));
				}
			}

		}
		return answer;
	}

	// turns raw classification into good one..?
	public static void sortAndWeed(int rank, ArrayList<Cell> array){
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
		
		int i = 0;
		while( i < array.size() ){
			int j = i + 1;
			while( j < array.size() && array.get(j) == null ){
				j++;
			}
			if( j == array.size() || ( Histogram.compareW( array.get(i), array.get(j) ) != 0 ) ){
				i = j;
			}
			else{
				variants = Permutations.allVariants(rank, array.get(i) );
				int k = 1;
				while( k < variants.size() && j < array.size() ){
					if( array.get(j) != null && Histogram.compareL(array.get(j), variants.get(k) ) == 0){
						array.set(j, null);
						k++;
					}
					j++;
				}
				i++;
				while( i < array.size() && array.get(i) == null){
					i++;
				}
			}
		}
		array = Utils.removeNulls(array);
	}
}
