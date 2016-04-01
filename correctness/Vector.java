import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
class VectorComparator implements Comparator<Vector>{
	public int compare(Vector x, Vector y){
		boolean lessThan = true;
		boolean greaterThan = true;
		for(int i = 0; i < x.myArray.length ; i++){
			if(x.myArray[i] > y.myArray[i]){
				lessThan = false;
			   break;
			}
		}
		for(int i = 0; i < x.myArray.length; i++){
			if(x.myArray[i] < y.myArray[i]){
				greaterThan = false;
			}
		}
		if(lessThan == false && greaterThan == false){
			System.out.println("NO!");
			System.exit(0);
		}
		
		if(lessThan == true){
			return -1;
		}
		
        return 1;
		
	}
}
public class Vector { 
	int [] myArray;
    public Vector(int [] arr){
    	myArray = arr;
    }
    public void setVector(int []arr){
    	myArray = arr;
    }
    public static void main(String args[]){
    	int numberOfNodes = Integer.parseInt(1);
    	int maxSize = Integer.parseInt(args[2]);
    	PriorityQueue<Vector> queue = new PriorityQueue(numberOfNodes*maxSize, new VectorComparator());
    	for(int i = 0; i < numberOfNodes; i++){
    		String filename = "foo-" + i + ".out";
    		FileReader reader = new FileReader(filename);
    		String line = null;
    		BufferedReader file = new BufferedReader(reader);
    		while((line = file.readLine()) != null){
    			 String tokens [] = line.split("[~]");
    			 tokens =  Arrays.copyOfRange(tokens, 1, tokens.length);
    			 int arr[] = new int[tokens.length];
    			 for(int i = 0; i < tokens.length; i++){
    				  arr[i] = Integer.parseInt(tokens[i]);
    			 }
    			 queue.add(new Vector(arr));
    		}
    	   file.close();
    	}
    	
    	System.out.println("NO CONCURRENT CRITICAL SECTIONS!");
    	
    }
}
