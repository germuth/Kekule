package bitvectors;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class Cell {
	/**
	 * Number of ports in the cell
	 */
	private int numPorts;
	/**
	 * Set of bit vectors, each representing a port assignment of this cell
	 */
	private BitVector[] portAssignments;
	
	public Cell(Set<BitVector> set, int numPorts){
		this.portAssignments = new BitVector[set.size()];
		int index = 0;
		Iterator<BitVector> i = set.iterator();
		while(i.hasNext()){
			this.portAssignments[index++] = (BitVector) i.next();
		}
		this.numPorts = numPorts;
	}
	
	public Cell(Cell c){
		this.portAssignments = c.portAssignments;
		this.numPorts = c.numPorts;
	}
	
	/**
	 * sorts this cell
	 */
	public void weightedSort(){
		//11 is rank limit
		//due to integer overflow
		//move to static variable
		int[] we = new int[11];
		//port assignments in array
		int x;
		
		for(int i = 0; i < this.size(); i++){
			x = this.portAssignments[i].countBits() - 1;
			for(int p = 0; p < this.numPorts; p++){
				if( ( ( this.portAssignments[i].getNumber() >> p) &  1 ) != 0){
					we[p] += x;
				}
			}
		}
		
		for(int i = 0; i < this.size(); i++){
			x = this.portAssignments[i].getNumber();
			this.portAssignments[i] = new BitVector(0);
			for(int p = 0; p < this.numPorts; p++){
				if( ( (x >> p) & 1) != 0){
					this.portAssignments[i] = new BitVector( this.portAssignments[i].getNumber() + we[p] );
				}
			}
			
		}
		
		Arrays.sort(this.portAssignments, new Comparator<BitVector>(){
			@Override
			public int compare(BitVector o1, BitVector o2) {
				if(o1.getNumber() < o2.getNumber()){
					return -1;
				}
				else if(o1.getNumber() > o2.getNumber()){
					return 1;
				}
				return 0;
			}
		});
		
	}
	
	/**
	 * Translates a cell over port assignment 
	 * @param portAssignment
	 */
	public void translate(BitVector translation){
		//if nothing to translate
		if(translation.isEmpty()){
			return;
		}
		//new set of translated port assignments
		BitVector[] translated = new BitVector[this.size()];
		
		//iterate over all cells current port assignments
		for(int i = 0; i < this.size(); i++){
			BitVector bv = this.portAssignments[i];
			//get symmetric difference of current and translation
			BitVector symD = BitVector.symmetricDifference(
					bv,
					translation);
			//place intersection in new port assignment set
			translated[i] = symD;
		}
		
		this.portAssignments = translated;
	}
	
	public void normalize(){
		int PA = this.center();
		this.sortBySize();
		BitVector pa = new BitVector(PA);
		System.out.println("Translated over \"" +pa.getPA(this.numPorts) + "\"" );
		System.out.println("And normalized gets:");
		Cell newKekule = Permutations.firstVariant(this);
		System.out.println(newKekule.toString());
	}
	
	/**
	 * Translate cell so it is centered
	 * @return
	 */
	public int center(){
		int k = 0;
		
		Cell wl = new Cell(this);
		wl.translate(this.portAssignments[0]);
		wl.weightedSort();
		
		for(int i = 1; i < this.portAssignments.length; i++){
			Cell owl = new Cell(this);
			owl.translate(this.portAssignments[i]);
			owl.weightedSort();
			
			//w2
			
			if( compareL(owl, wl) < 0 ){
				k = i;
				wl = null;
				wl = owl;
			}
			else{
				owl = null;
			}
			
			//w3
			
		}
		
		wl = null;
		int portAssignment = this.portAssignments[k].getNumber();
		this.translate(this.portAssignments[k]);
		return portAssignment;
	}
	
	public int compareL(Cell one, Cell two){
		
		int diff = one.portAssignments.length - two.portAssignments.length;
		if(diff != 0){
			return diff;
		}
		
		return compareLex(one.portAssignments.length, one, two);
	}
	
	public int compareLex(int diff, Cell one, Cell two){
		diff--;
		while(diff >= 0 && one.portAssignments[diff].equals(two.portAssignments[diff])){
			diff--;
		}
		if(diff < 0){
			return 0;
		}
		return one.portAssignments[diff].getNumber() - two.portAssignments[diff].getNumber();
	}
	
	public void printList(){
		String answer = "";
		for(int i = 0; i < this.size(); i++){
			answer += this.portAssignments[i].getNumber() + " ";
		}
		System.out.println(answer);
	}
	
	public void sortBySize(){
		Arrays.sort(this.portAssignments, new Comparator<BitVector>(){
			@Override
			public int compare(BitVector one, BitVector two){
				if(one.getNumber() < two.getNumber()){
					return -1;
				}
				else if(one.getNumber() > two.getNumber()){
					return 1;
				}
				return 0;
			}
		});
	}
	
	public String printUnweighted(){
		String[] portAssigns = new String[this.size()];
		int index = 0;
		int[] bitVectors = new int[this.size()];
		String answer = "Unweighted Cell: ";

		for (BitVector bv : this.portAssignments) {
			bitVectors[index++] = bv.getNumber();
		}

		Arrays.sort(bitVectors);
		index = 0;

		for (int number : bitVectors) {
			if (number == 0) {
				portAssigns[index] = "0";
				index++;
			} else {
				String ports = "";
				for (int j = this.numPorts; j > 0; j--) {
					if ((number & (1 << (j - 1))) != 0) {
						ports += (char) ('a' + j - 1);
					}
				}
				ports = Utils.sort(ports);
				portAssigns[index] = ports;
				index++;
			}
		}

		for (int k = 0; k < portAssigns.length; k++) {
			answer += portAssigns[k] + " ";
		}
		return answer;

	}
	
	public String toString() {
		return printUnweighted();
	}

	public int size(){
		return this.portAssignments.length;
	}
	
	public int getNumPorts() {
		return numPorts;
	}

	public void setNumPorts(int numPorts) {
		this.numPorts = numPorts;
	}
	
	public BitVector[] getPortAssignments(){
		return this.portAssignments;
	}
}
