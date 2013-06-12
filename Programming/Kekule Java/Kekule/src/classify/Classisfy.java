package classify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import makeCell.Histogram;

import shared.BitVector;
import shared.Cell; 

public class Classisfy {

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
		}
		if(option != 1){
			sortAndWeed(rank, classifications);
			freePerm();
			for(int i= 0; i < classifications.size(); i++){
				System.out.println(classifications.get(i));
			}
		}
	}
	
	public static ArrayList<Cell> classify(int rank, int option){
		
		ArrayList<Cell> answer = new ArrayList<Cell>();
		
		BitVector ports = new BitVector(1 << rank);
		Cell even = Cell.allEvenPA(rank);
		
		Set<BitVector> candSet = new HashSet<BitVector>();
		candSet.add(new BitVector(0));
		
		long ulimit = (1 << ( even.size() - 1)); 
		
		Cell cand = new Cell(candSet, rank);
		Histogram.rank = rank;
		
		for(long x = 0; x < ulimit; x++){
			long y = x;
			int i = 1;
			while( y > 0 ){
				if( y % 2 == 1){
					cand.add(even.getPA()[i]);
				}
				y /= 2;
				i++;
			}
			
			if( cand.isFlexible(ports) && Histogram.isPortHistoDescending(cand)
					&& Histogram.isCentered(cand) && Coherence.isCoherent(cand)){
				if(option != 0){
					//printCell()
				}
				else{
					cand.sortBySize();
					answer.add(new Cell(cand));
				}
			}
		}
		return answer;
	}
	
	//turns raw classification into good one..?
	public static void sortAndWeed(int rank, ArrayList array){
		
	}
}
