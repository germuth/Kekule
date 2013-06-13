package makeGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class cellToGraph {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		File f = new File("myraw.txt");
		Scanner s = new Scanner(f);
		
		//constuct matched graphs
		int internal = 4;
		setAMG(internal);
		
		s.close();

	}

}
