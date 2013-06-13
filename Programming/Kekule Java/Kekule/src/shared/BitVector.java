package shared;



/**
 * BitVector
 * 
 * This class represents the bit vector data structure. It is an array data 
 * structure. All data is held within
 * an integer. In in the integers binary form, each 1 indicates the presence of 
 * an object, and a zero represents the lack there of. For example, a set of 3 nodes
 * from the total of 5 could look like this:
 * Total : 11111 (31) SubSet: 01101 (13)
 * 
 * This subset contains objects 1, 3, and 4. This is used to represent edges 
 * (a bitvector with only two occurences), set of nodes (as above), and others.
 * @author Aaron
 *
 */
public class BitVector {
	
	//STATIC
	
	/**
	 * Static method to take the symmetric difference between two bitvectors.
	 * Doesn't edit either bitVector, returns a new one.
	 * @param a, first Bitvector
	 * @param b, second BitVector
	 * @return, their symmetric difference
	 */
	public static BitVector symmetricDifference(BitVector a, BitVector b){
		//^ is exclusive or
		//one or or other but not both
		//which is symmetric difference
		int symD = a.number ^ b.number;
		return new BitVector(symD);
	}
	/**
	 * Static method to take the intersection of two bitVectors. Does not edit
	 * either BitVector, returns a new one.
	 * @param a, first BitVector
	 * @param b, second BitVector
	 * @return their intersection
	 */
	public static BitVector intersection(BitVector a, BitVector b){
		//& is bitwise and
		//not one but both
		int inter = a.number & b.number;
		return new BitVector(inter);
	}
	/**
	 * Static method to take the union of two bitvectors. Doesn't edit either 
	 * BitVector, returns a new one represnting the union.
	 * @param a, first BitVector
	 * @param b, Second BitVector
	 * @return, their union
	 */
	public static BitVector union(BitVector a, BitVector b){
		// | is bitwise operator
		//from a, b, or both
		int union = a.number | b.number;
		return new BitVector(union);
	}
	
	//NON-STATIC
	
	/**
	 * Integer representing the bitVector 
	 */
	private final int number;
	
	/**
	 * Default Constructor
	 * @param b, integer
	 */
	public BitVector(int b){
		this.number = b;
	}
	
	/**
	 * Constuctor
	 * Copy's a bitVector from another one
	 * @param another
	 */
	public BitVector(BitVector another){
		this.number = another.number;
	}
	
	/**
	 * Counts the number of occurrences in the bitVector. Could be used
	 * to determine the amount of elements in a subset, ect.
	 * 
	 * For example, 1000 1001, would return 3
	 * @return, number of ones in the binary representation of this BitVector
	 */
	public int countBits(){
		int amount = 0;
		int x = this.number;
		while( x > 0 ){
			amount += x % 2;
			x /= 2;
		}
		return amount;
	}
	
	
	/**
	 * Returns the first 1 is this bitvector. Does not remove, only returns. 
	 * For Example, 0010 1100 would return 0000 0100 or 4
	 * @return first 1 in bitvector from least significant to most significant bit
	 */
	public int firstNode(){
		//no node to find
		if(this.isEmpty()){
			return 0;
		}
		int k = 1;
		//while bit vector and index k have no common 1
		//double k every time to shift bit 
		while(( this.number & k ) == 0){
			k *= 2;
		}
		return k;
	}
	
	/**
	 * Returns a new BitVector which is identical to this one, except
	 * n has now been removed.
	 * @param n, element to remove 
	 */
	public BitVector remove(int n){
		//^ is exclusize or
		//since n is node we want removed
		//ie 000010000
		//and bitvector is something like this
		//   100110001
		//exclusive or gets
		//   100100001
		//which effectively removes that node
		return new BitVector(this.number ^ n);
	}
	
	/**
	 * Returns whether this bitvector contains an element with number n
	 * @param n, what we are searching for
	 * @return, whether we found it
	 */
	public boolean contains(int n){
		//use bitwise and operator
		//n  = 00 1000
		//bv = 11 1001
		//&  = 00 1000
		//which is non-zero number
		//so it contains it
		if( ( this.number & n )== 0){
			return false;
		}
		return true;
	}
	
	/**
	 * ismember
	 */
	public boolean isMember(Cell c){
		BitVector[] array = c.getPA();
		for(int i = 0; i < array.length; i++){
			if(array[i].equals(this)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Simple to string which prints out the bit Vectors number in decimal
	 * and binary form
	 */
	public String toString(){
		return this.number + " " + "(" + Integer.toBinaryString(number) + ")";
	}
	
	/**
	 * Returns a string of the port assignments of this Bit Vector. 
	 * Assumes least significant bit represents 'a'. 
	 * @param numPorts
	 * @return
	 */
	public String getPA(int numPorts){
		if(this.number == 0){
			return "0";
		}
		else{
			String ports = "";
			for(int j = numPorts; j > 0; j--){
				if( ( number & (1 << (j-1) ) ) != 0){
					ports += (char)('a' + j - 1);
				}
			}
			ports = Utils.sort(ports);
			return ports;
		}
	}
	
	/**
	 * Returns a hashCode of this Bitvector, simply as it's number. Since HashSets 
	 * of BitVectors are used, this will cause identical BitVectors to hash to the
	 * same place, and avoid duplicates. 
	 * (Sets aren't supposed to have duplicates)
	 */
	@Override
	public int hashCode() {
		return this.number;
	}
	
	/**
	 * Returns whether this BitVector is equal to a given BitVector. Only trait
	 * is whether they share the same number
	 */
	@Override
	public boolean equals(Object obj){
		BitVector another = (BitVector) obj;
		if(this.number == another.number){
			return true;
		}
		return false;
	}
	
	/**
	 * Returns whether this bitVector is empty, as in 
	 * a value of 0, no ones.
	 * @return
	 */
	public boolean isEmpty(){
		if(this.number == 0){
			return true;
		}
		return false;
	}

	public int getNumber() {
		return number;
	}
}
