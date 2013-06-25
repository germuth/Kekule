package makeCell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import shared.Cell;
public class Test {
	
	public static void main(String[] args){
		
		Set<Integer> allPossiblePortSelections = new HashSet<Integer>();
		for(int i = 1; i <=5 ; i++){
			allPossiblePortSelections.add(i);
		}

		PowerSet<Integer> powerset = new PowerSet<Integer>(
				allPossiblePortSelections, 2,2);
		Iterator<Set<Integer>> graphsI = powerset.iterator();
		int index = 0;
		while( graphsI.hasNext() ){
			
			Set<Integer> set = graphsI.next();
			if(set.isEmpty()){
				continue;
			}
			Iterator<Integer> inSet = set.iterator();
			int first = inSet.next();
			int second = inSet.next();
			
			System.out.println("triple 5s11");
			System.out.println("16 5");
			System.out.println("1 2 3 4 5");
			System.out.println("0-6-7-8-9-10-11-12-13-14-15-0-9");
			System.out.println("10-14, 1-7, 2-8, 3-11, 4-12, 5-15, " + first + "-" + second);
			System.out.println("");
		}
	}

	public static void main7(String[] args) throws IOException{
		File f = new File("them.txt");
		f.createNewFile();
		PrintWriter pw = new PrintWriter(f);
		
		Set<Integer> allPossiblePortSelections = new HashSet<Integer>();
		for(int i = 1; i <= 7; i++){
			allPossiblePortSelections.add(i);
		}

		PowerSet<Integer> powerset = new PowerSet<Integer>(
				allPossiblePortSelections, 5,5);

		Iterator<Set<Integer>> graphsI = powerset.iterator();
		int index = 0;
		while( graphsI.hasNext() ){
			
			Set<Integer> set = graphsI.next();
			if(index == 0){
				index++;
				continue;
			}
			//System.out.println(set);
			pw.println("triple 5s" + index);
			pw.println("11 5");
			Iterator<Integer> setI = set.iterator();
			while( setI.hasNext()){
				pw.print( (setI.next()) + " ");
			}
			pw.println("");
			
			pw.println("0-1-2-3-8-9-4-5-6-10-7-0-8");
			pw.println("9-10");
			pw.println("");
			
			pw.flush();
			index++;
		}
		pw.close();
	}

	public static void main6(String[] args) {
		Scanner s = new Scanner(System.in);
		int count = 0;
		int sum = 0;
		String in = s.next();
		while (!in.contains("!")) {
			sum += Integer.parseInt(in);
			in = s.next();
			count++;
		}
		System.out.println(sum / count);
	}

	public static void main5(String[] args) throws FileNotFoundException {
		ArrayList<Integer> list = new ArrayList<Integer>();
		File f = new File("themraw.txt");
		Scanner s = new Scanner(f);

		while (s.hasNext()) {
			String temp = s.next();
			temp = temp.trim();
			int next = -1;
			try {
				next = Integer.parseInt(temp);
			} catch (Exception e) {
				continue;
			}
			list.add(next);
		}

		ArrayList<Integer> list2 = new ArrayList<Integer>();
		File f2 = new File("myraw.txt");
		Scanner s2 = new Scanner(f2);
		while (s2.hasNext()) {
			list2.add(Integer.parseInt(s2.next()));
		}

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) != list2.get(i)) {
				System.out.println("bad " + i);
				System.out.println(list.get(i));
				System.out.println(list2.get(i));
			}
		}
		System.out.println("DONE");

	}

	public static void main4(String[] args) {
		for (int i = 0; i < 32000; i++) {
			System.out.println((double) i / (double) 32000);
		}
	}

	public static void main3(String[] args) {
		ArrayList<Cell> array = new ArrayList<Cell>();
		for (int i = 0; i < 10; i++) {
			array.add(new Cell());
		}
		Object[] meow = array.toArray();
		if (meow instanceof Cell[]) {
			Cell[] newo = (Cell[]) meow;
			System.out.println(newo);
			System.exit(1);
		}
		System.out.println("THE END");
	}

	public static void main2(String[] args) throws FileNotFoundException {
		File mine = new File("mine.txt");
		Scanner mS = new Scanner(mine);
		ArrayList<String> myStrings = new ArrayList<String>();
		while (mS.hasNext()) {
			String in = mS.nextLine();
			if (in.contains("Cell:") && in.charAt(0) == 'C') {
				String substr = in.substring(6);
				substr = substr.trim();
				myStrings.add(substr + ".");
			}
		}

		File theirs = new File("them.txt");
		Scanner tS = new Scanner(theirs);
		ArrayList<String> themStrings = new ArrayList<String>();

		while (tS.hasNext()) {
			String in = tS.nextLine();
			if (in.contains("[ ")) {
				String substr = in.substring(19);
				themStrings.add(substr);
			}
		}

		for (int i = 0; i < themStrings.size(); i++) {
			if (!themStrings.get(i).equals(myStrings.get(i))) {
				System.out.println("bad");
				System.out.println(themStrings.get(i) + " and "
						+ myStrings.get(i));
			}
		}

		/*
		 * String input =
		 * " 33   3 514 516  12 1032 1040  80 2112 2176 384 288 8224 12288 6144 8704 5120"
		 * ; Scanner s = new Scanner(input); while(s.hasNext()){ String num =
		 * s.next(); int number = Integer.parseInt(num);
		 * System.out.println(Integer.toBinaryString(number)+ " "); }
		 */
		// Set<BitVector> test = new Set<BitVector>();
	}
}

class PowerSet<E> implements Iterator<Set<E>>, Iterable<Set<E>> {
	private E[] arr = null;
	private BitSet bset = null;
	private int minSize;
	private int maxSize;

	@SuppressWarnings("unchecked")
	public PowerSet(Set<E> set, int min, int max) {
		arr = (E[]) set.toArray();
		bset = new BitSet(arr.length + 1);
		this.minSize = min;
		this.maxSize = max;
	}

	@Override
	public boolean hasNext() {
		return !bset.get(arr.length);
	}

	@Override
	public Set<E> next() {
		Set<E> returnSet = new TreeSet<E>();
		// System.out.println(printBitSet());
		for (int i = 0; i < arr.length; i++) {
			if (bset.get(i)) {
				returnSet.add(arr[i]);
			}
		}

		int count;
		do {
			incrementBitSet();
			count = countBitSet();
		} while ((count < minSize) || (count > maxSize));

		return returnSet;
	}

	protected void incrementBitSet() {
		for (int i = 0; i < bset.size(); i++) {
			if (!bset.get(i)) {
				bset.set(i);
				break;
			} else
				bset.clear(i);
		}
	}

	protected int countBitSet() {
		int count = 0;
		for (int i = 0; i < bset.size(); i++) {
			if (bset.get(i)) {
				count++;
			}
		}
		return count;

	}

	protected String printBitSet() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < bset.size(); i++) {
			if (bset.get(i)) {
				builder.append('1');
			} else {
				builder.append('0');
			}
		}
		return builder.toString();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Not Supported!");
	}

	@Override
	public Iterator<Set<E>> iterator() {
		return this;
	}
}

/*
 * 


//System.out.println(set);
			System.out.println("Fluorene" + index);
			System.out.println("13 5");
			Iterator<Integer> setI = set.iterator();
			while( setI.hasNext()){
				System.out.print( (setI.next()) + " ");
			}
			System.out.println("");
			
			System.out.println("0-1-10-2-3-4-5-12-11-6-7-8-9-0-11");
			System.out.println("10-12");
			System.out.println("");
			
			index++;


System.out.println("Anthracene" + index);
			System.out.println("14 5");
			Iterator<Integer> setI = set.iterator();
			while( setI.hasNext()){
				System.out.print( (setI.next()) + " ");
			}
			System.out.println("");
			
			System.out.println("0-1-12-2-3-4-5-13-6-11-7-8-9-10-0-11");
			System.out.println("12-13");
			System.out.println("");


//System.out.println(set);
			System.out.println("Phenanthrene" + index);
			System.out.println("14 5");
			Iterator<Integer> setI = set.iterator();
			while( setI.hasNext()){
				System.out.print( (setI.next()) + " ");
			}
			System.out.println("");
			
			System.out.println("0-1-2-12-3-4-5-6-13-11-7-8-9-10-0-11");
			System.out.println("12-13");
			System.out.println("");
			

//System.out.println(set);
			System.out.println("Pyrene" + index);
			System.out.println("16 5");
			Iterator<Integer> setI = set.iterator();
			while( setI.hasNext()){
				System.out.print( (setI.next()) + " ");
			}
			System.out.println("");
			
			System.out.println("0-1-2-3-11-4-5-15-6-7-8-14-9-10-0-12-13-14");
			System.out.println("11-12, 13-15");
			System.out.println("");

System.out.println("Benz(a)anthracene" + index);
			System.out.println("18 5");
			Iterator<Integer> setI = set.iterator();
			while( setI.hasNext()){
				System.out.print( (setI.next()) + " ");
			}
			System.out.println("");
			
			System.out.println("0-14-1-16-2-3-4-5-17-6-15-7-8-13-9-10-11-12-0-13");
			System.out.println("14-15, 16-17");
			System.out.println("");


System.out.println("Fluoranthene" + index);
			System.out.println("16 5");
			Iterator<Integer> setI = set.iterator();
			while( setI.hasNext()){
				System.out.print( (setI.next()) + " ");
			}
			System.out.println("");
			
			System.out.println("0-11-1-2-3-15-4-5-6-13-12-7-8-9-10-0-12");
			System.out.println("11-14, 13-14, 14-15");
			System.out.println("");

	System.out.println("Benzo(a)pyrene" + index);
			System.out.println("20 5");
			Iterator<Integer> setI = set.iterator();
			while( setI.hasNext()){
				System.out.print( (setI.next()) + " ");
			}
			System.out.println("");
			
			System.out.println("0-1-2-3-14-4-5-16-6-19-7-8-9-10-18-17-11-12-0-13-15-16");
			System.out.println("13-14, 15-17, 18-19");
			System.out.println("");


System.out.println("Benzo(e)pyrene" + index);
			System.out.println("20 5");
			Iterator<Integer> setI = set.iterator();
			while( setI.hasNext()){
				System.out.print( (setI.next()) + " ");
			}
			System.out.println("");
			
			System.out.println("0-1-2-3-4-14-16-5-6-7-18-8-9-19-10-11-12-13-0-14");
			System.out.println("13-15, 15-19, 15-17, 16-17, 17-18");
			System.out.println("");


System.out.println("Benzo(b)fluoranthene" + index);
			System.out.println("20 5");
			Iterator<Integer> setI = set.iterator();
			while( setI.hasNext()){
				System.out.print( (setI.next()) + " ");
			}
			System.out.println("");
			
			System.out.println("0-1-2-3-17-18-4-5-6-7-19-16-8-14-9-10-11-12-13-0-15-17");
			System.out.println("13-14, 15-16, 18-19");
			System.out.println("");


	pw.println("Benzo(ghi)perylene" + index);
			pw.println("22 5");
			Iterator<Integer> setI = set.iterator();
			while( setI.hasNext()){
				pw.print( (setI.next()) + " ");
			}
			pw.println("");
			
			pw.println("0-1-2-3-14-15-4-5-6-19-7-8-21-9-10-20-11-12-0-13-16-17-21");
			pw.println("13-14, 16-20, 17-18, 15-18, 18-19");
			pw.println("");



 */