package graphs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import classification.Classify;


import shared.BitVector;
import shared.Cell;
import shared.Histogram;
import shared.Utils;

public class ClassifyGraph {

	private static int UPB = 11;
	//aMG[2*i] holds the isomorphism classes of matched 
	//grahs of rank 2 * i
	//aMG[2*i-1] can hold the isomorphism classes of nonmatced graphs
	//of rank 2 * i
	private static ArrayList<Cell> aMG[] = new ArrayList[UPB];
	
	private static boolean aMGisMade = false;

	public static void freeAMG(){
		ClassifyGraph.aMG = new ArrayList[UPB];
	}
	
	public static void setAMG(int k){
		for(int i = 2; i <= k; i += 2){
			aMG[i] = allMatchedGraphs(i);
		}
		ClassifyGraph.aMGisMade = true;
	}
	/**
	 * get ALL Matched Graphs
	 * @param k
	 * @return
	 */
	public static ArrayList<Cell> getAMG(int k){
		return aMG[k];
	}
	
	public static ArrayList<Cell> allMatchedGraphs(int rank){
		ArrayList<Cell> array = allGraphs(rank);
		for(int i = 0; i < array.size(); i++){
			if( !matched( rank, array.get(i) ) ){
				array.set(i, null);
			}
		}
		array = Utils.removeNulls(array);
		return array;
	}
	
	public static boolean matched(int rank, Cell gr){
		Cell grc = new Cell(gr);
		return matchedR( ( 1 << rank ) - 1, grc );	
	}
	
	public static boolean matchedR(int cover, Cell gr){
		if( cover == 0){
			return true;
		}
		if(gr.size() == 0){
			return false;
		}
		BitVector ed = gr.removeLast();
		//int size = gr.size();
		if( matchedR( cover, gr ) ){
			return true;
		}
		if( ( ed.getNumber() | cover ) != cover){
			return false;
		}
		cover ^= ed.getNumber();
		return matchedR( cover, gr );
	
	}
	
	
	public static ArrayList<Cell> allGraphs(int rank){
		ArrayList<Cell> answer = new ArrayList<Cell>();
		
		Cell fg = fullGraph(rank);
		long ulim = 1 << fg.size();
		Histogram.rank = rank;
		for(int x = 0; x < ulim; x++){

			Cell cand = new Cell();
			cand.setNumPorts(rank);
			
			int y = x;
			int i = 0; 
			while( y > 0 ){
				if( y % 2 == 1){
					cand.add(new BitVector(fg.getPA()[i]));
				}
				y /= 2;
				i++;
			}
			if ( Histogram.isPortHistoDescending(cand)){
				answer.add(cand);
			}
			
		}
		answer = Classify.sortAndWeedGraphs(rank, answer);
		return answer;
	}
	
	public static Cell fullGraph(int rank){
		Set<BitVector> fullGraph = new HashSet<BitVector>();
		int qlimit = 1 << rank;
		for(int q = 2; q < qlimit; q <<= 1 ){
			for(int p = 1; p < q; p <<= 1){
				BitVector pQ = new BitVector(p+q);
				fullGraph.add(pQ);
			}
		}
		return new Cell(fullGraph, rank);
	}
}
