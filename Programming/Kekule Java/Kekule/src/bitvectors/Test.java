package bitvectors;

import java.util.Scanner;

public class Test {
	public static void main(String[] args){
		String input = " 33   3 514 516  12 1032 1040  80 2112 2176 384 288 8224 12288 6144 8704 5120";
		Scanner s = new Scanner(input);
		while(s.hasNext()){
			String num = s.next();
			int number = Integer.parseInt(num);
			System.out.println(Integer.toBinaryString(number)+ " ");
		}
		
	}
}
