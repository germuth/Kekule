import java.util.Set;
import java.util.TreeSet;


public class Utils {
	
	public static Set<Integer> symmetricDifference(Set<Integer> one, Set<Integer> two){
		//in order to get symmetric difference
		//we add two sets together
		//then subtract the intersection
		
		Set<Integer> symmetricDiff = new TreeSet<Integer>(one);
		//the sum of both of them (one + two)
		symmetricDiff.addAll(two);
		
		Set<Integer> temp = new TreeSet<Integer>(one);
		//the intersection of one and two
		temp.retainAll(two);
		
		// the symmetric difference
		symmetricDiff.removeAll(temp);
		
		return symmetricDiff;
	}
}