package bitvectors;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Test {
	public static void main(String[] args) throws FileNotFoundException{
		File mine = new File("mine.txt");
		Scanner mS = new Scanner(mine);
		ArrayList<String> myStrings = new ArrayList<String>();
		while(mS.hasNext()){
			String in = mS.nextLine();
			if(in.contains("Cell:") && in.charAt(0) == 'C'){
				String substr = in.substring(6);
				substr = substr.trim();
				myStrings.add(substr + ".");
			}
		}
		
		File theirs = new File("them.txt");
		Scanner tS = new Scanner(theirs);
		ArrayList<String> themStrings = new ArrayList<String>();
		
		while(tS.hasNext()){
			String in = tS.nextLine();
			if(in.contains("[ ")){
				String substr = in.substring(19);
				themStrings.add(substr);
			}
		}
		
		for(int i = 0; i < themStrings.size(); i++){
			if(!themStrings.get(i).equals(myStrings.get(i))){
				System.out.println("bad");
				System.out.println( themStrings.get(i) + " and " + myStrings.get(i));
			}
		}
		
		/*
		String input = " 33   3 514 516  12 1032 1040  80 2112 2176 384 288 8224 12288 6144 8704 5120";
		Scanner s = new Scanner(input);
		while(s.hasNext()){
			String num = s.next();
			int number = Integer.parseInt(num);
			System.out.println(Integer.toBinaryString(number)+ " ");
		}
		*/
		//Set<BitVector> test = new Set<BitVector>();
	}
}
