package shared;
import graphs.Graph;
import gui.MutateMain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
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
	public static ArrayList<BitVector> removeDups(ArrayList<BitVector> list){
		for(int i = 0; i < list.size(); i++){
			BitVector node1 = list.get(i);
			
			for(int j = i + 1; j < list.size(); j++){
				BitVector node2 = list.get(j);
				
				if(node1.equals(node2)){
					list.remove(j);
					j--;
				}
			}
		}
		
		return list;
	}
	public static ArrayList<Graph> getRank6Graphs(MutateMain pp){
		ArrayList<Graph> graphs = new ArrayList<Graph>();
		int graphCount = 1;
		//InputStream is = pp.getClass().getResourceAsStream("Rank6HesselinkGraphs.txt");
	    //InputStreamReader isr = new InputStreamReader(is);
		//InputFlatFile currentFile = new InputFlatFile("theFile.txt");
		File f = new File("Rank6HesselinkGraphs.txt");
		try {
			//Scanner s = new Scanner(isr);
			Scanner s = new Scanner(f);
			
			while(s.hasNextLine()){
				String next = s.nextLine();
				if( next.contains("Gra6nr")){
					Scanner s1 = new Scanner(next);
					s1.next();
					int numPorts = s1.nextInt();
					String numNodesO = s1.next();
					numNodesO = numNodesO.substring(0, numNodesO.length() - 1);
					int numNodes = Integer.parseInt(numNodesO);
					Set<String> edges = new TreeSet<String>();
					while( s1.hasNext()){
						String temp = s1.next();
						temp = temp.replace(",", " ");
						temp = temp.replace(".", " ");
						temp = temp.replace("-", " ");
						edges.add(temp);
					}
					
					Graph current = new Graph("graph" + graphCount++, numPorts, numNodes, edges);
					graphs.add(current);
				}else if(next.contains("No graph found")){
					//Hesselink didn't find every graph
					//still want to associate correct numbers which each one though
					graphCount++;
				}
			}
		} catch( FileNotFoundException ex){
			ex.printStackTrace();
		}
		
		return graphs;
	}
	
	public static double percent(int p){
		double per = (double)p/(double)100.0;
		return per;
	}
	
	public static int getLastSender(String packet){
		String[] nodes = packet.split(" ");
		String last = nodes[nodes.length - 1];
		int num = Integer.parseInt( last );
		return num;
	}
	
	public static int getPacketLength(String packet){
		return packet.split(" ").length;
	}
	
	public static class Pair<F, S> {
	    private F first; //first member of pair
	    private S second; //second member of pair

	    public Pair(F first, S second) {
	        this.first = first;
	        this.second = second;
	    }

	    public void setFirst(F first) {
	        this.first = first;
	    }

	    public void setSecond(S second) {
	        this.second = second;
	    }

	    public F getFirst() {
	        return first;
	    }

	    public S getSecond() {
	        return second;
	    }
	}
	
	public static int binomial(int n, int K) {
	    int ret = 1;
	    for (int k = 0; k < K; k++) {
	    	ret = ret*(n-k)/(k+1);
	    }
	    return ret;
	}
	
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
	
	/**
	 * Prints an array list in BitVector number form
	 * @param variants
	 */
	public static void printArrayList(ArrayList<Cell> variants){
		System.out.println("Size: " + variants.size());
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
	 * Parses through an ArraylIst and adds every unique element to a new list. Returns
	 * the new list to you, the original list is un edited. Works on the generic type
	 * by calling .equals() method.
	 * @param list to have duplicates removed from
	 * @return a new list without duplicates ( as specified by object's .equals() method)
	 */
	public static <T> ArrayList<T> deleteDuplicates(ArrayList<T> list){
		if(list.size() < 2){
			return list;
		}
		
		ArrayList<T> newList = new ArrayList<T>();
		newList.add(list.get(0));
		for(int i = 0; i < list.size(); i++){
			
			boolean found = false;
			
			inner:
			for(int j = 0; j < newList.size(); j++){
				if(newList.get(j).equals(list.get(i))){
					found = true;
					break inner;
				}
			}
			if(!found){
				newList.add(list.get(i));
			}
		}
	
		return newList;
	}
	
	/**
	 * Removes all null entries from array list by creating a new arraylist and only 
	 * adding elements which are non-null. Returns this new arraylist
	 * @param <T>
	 */
	public static <T> ArrayList<T> removeNulls(ArrayList<T> array){
		ArrayList<T> newList = new ArrayList<T>();
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
