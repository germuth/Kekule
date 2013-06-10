package bitvectors;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @author Aaron
 *
 */
public class Utils {
	
	/**
	 * 
	 * @param unsorted
	 * @return
	 */
	public static String sort(String unsorted){
		char[] chars = unsorted.toCharArray();
		Arrays.sort(chars);
		return new String(chars);
	}
	
	/**
	 * Translates a set over a bitvector
	 * @param portAssignment
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
	 *  
	 * @param a
	 * @param b
	 * @return
	 */
	public static Set<BitVector> union(Set<BitVector> a, Set<BitVector> b){
		Set<BitVector> union = new HashSet<BitVector>();
		union.addAll(a);
		union.addAll(b);
		return union;
	}
}
