package bitvectors;

public class Histogram {
	public static int rank;
	
	public static void portHisto(Cell cell, int[] hist){
		for(int i = 0; i < rank; i++){
			//WOAH this array might be too big
			hist[i] = 0;
		}
		for(int j = 0; j < cell.size(); j++){
			BitVector current = cell.getPortAssignments()[j];
			int k = current.getNumber();
			//if rank 5
			//w:= gives 103
			//w:= cntBits gives 38
			//w:= cntBits +1 only gives 32!
			int w = current.countBits() + 1;
			int i = 0;
			while(k > 0){
				if(k % 2 != 0){
					hist[i] += w;
				}
				k /= 2;
				i++;
			}
		}
	}
	
}
