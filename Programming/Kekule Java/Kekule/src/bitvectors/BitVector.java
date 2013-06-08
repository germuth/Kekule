package bitvectors;

public class BitVector {
	/**
	 * Integer representing bitVector 
	 */
	private int number;
	
	public static BitVector symmetricDifference(BitVector a, BitVector b){
		//^ is exclusive or
		//one or or other but not both
		//which is symmetric difference
		int symD = a.number ^ b.number;
		return new BitVector(symD);
	}
	
	public static BitVector intersection(BitVector a, BitVector b){
		//& is bitwise and
		//not one but both
		int inter = a.number & b.number;
		return new BitVector(inter);
	}
	
	public static BitVector union(BitVector a, BitVector b){
		// | is bitwise operator
		//from a, b, or both
		int union = a.number | b.number;
		return new BitVector(union);
	}
	
	public BitVector(int b){
		this.number = b;
	}
	
	public BitVector(BitVector another){
		this.number = another.number;
	}
	
	public String toString(){
		return this.number + " " + "(" + Integer.toBinaryString(number) + ")";
	}
	
	@Override
	public boolean equals(Object obj){
		BitVector another = (BitVector) obj;
		if(this.number == another.number){
			return true;
		}
		return false;
	}
	
	public boolean isEmpty(){
		if(this.number == 0){
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the first node contained in this bitvector
	 * does not remove it from the bitvector
	 * @return
	 */
	public int firstNode(){
		//no node to give
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
	 * Removes node n from this bitvector
	 * @param n
	 */
	public void remove(int n){
		//^ is exclusize or
		//since n is node we want removed
		//ie 000010000
		//and bitvector is something like this
		//   100110001
		//exclusive or gets
		//   100100001
		//which effectively removes that node
		this.number ^= n;
	}
	
	/**
	 * Returns whether this bitvector contains 
	 * node n
	 * @param n
	 * @return
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

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}