package makeCell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import shared.Cell;

public class Test {
	
	public static void main(String[] args) throws FileNotFoundException{
		ArrayList<Integer> list = new ArrayList<Integer>();
		File f = new File("themraw.txt");
		Scanner s = new Scanner(f);

		while(s.hasNext()){
			String temp = s.next();
			temp = temp.trim();
			int next = -1;
			try{
				next = Integer.parseInt(temp);
			}catch(Exception e){
				continue;
			}
			list.add(next);
		}
		
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		File f2= new File("myraw.txt");
		Scanner s2 = new Scanner(f2);
		while(s2.hasNext()){
			list2.add(Integer.parseInt(s2.next()));
		}
		
		for(int i = 0; i < list.size(); i++){
			if(list.get(i) != list2.get(i)){
				System.out.println("bad " + i);
				System.out.println(list.get(i));
				System.out.println(list2.get(i));
			}
		}
		System.out.println("DONE");
		
	}
	public static void main4(String[] args){
		for(int i = 0; i < 32000; i++){
			System.out.println((double)i / (double) 32000);
		}
	}
	public static void main3(String[] args){
		ArrayList<Cell> array = new ArrayList<Cell>();
		for(int i = 0; i < 10; i++){
			array.add(new Cell());
		}
		Object[] meow = array.toArray();
		if(meow instanceof Cell[]){
			Cell[] newo = (Cell[]) meow;
			System.out.println(newo);
			System.exit(1);
		}
		System.out.println("THE END");
	}
	
	public static void main2(String[] args) throws FileNotFoundException{
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
