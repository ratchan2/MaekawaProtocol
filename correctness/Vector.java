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
			System.out.println("NO! PROTOCOL DOES NOT WORK!");
			System.exit(0);
		}
		
		if(lessThan == true){
			if(x.pid == y.pid && x.type.equals("L") && y.type.equals("E")){
				boolean equals = true;
			    for(int i = 0; i < x.myArray.length; i++){
			    	if(x.myArray[i] != y.myArray[i]){
			    		equals = false;
			    		break;
			    	}
			    }
				 if(equals == true){
					 return 1;
				 }
			}
			return -1;
		}
		
        return 1;
		
	}
}
public class Vector { 
	int [] myArray;
	String type;
	int pid;
	String line;
    public Vector(String line){
    	String tokens[]  = line.split("[~]");
    	    this.line = line;
    	    type = tokens[0];
    	    pid = Integer.parseInt(tokens[1]);
    	    myArray = new int[tokens.length -2];
    	for(int i = 0; i < myArray.length; i++){
    		myArray[i] = Integer.parseInt(tokens[i+2]);
    	}    
    }
    public void setVector(int []arr){
    	myArray = arr;
    }
    public static void main (String args[]) throws Exception{
    	int numberOfNodes = Integer.parseInt(args[0]);
    	int maxSize = Integer.parseInt(args[1]);
    	//PriorityQueue<Vector> queue = new PriorityQueue(numberOfNodes*maxSize, new VectorComparator());
    	ArrayList<Vector> list = new ArrayList<Vector>();
    	for(int i = 0; i < numberOfNodes; i++){
    		String filename = "foo-" + i + ".out";
    		FileReader reader = new FileReader(filename);
    		String line = null;
    		BufferedReader file = new BufferedReader(reader);
    		while((line = file.readLine()) != null){
    			 list.add(new Vector(line));
    		}
    	   file.close();
    	}
    	
    	Collections.sort(list, new VectorComparator());
    	Iterator<Vector> iterate = list.iterator() ;
    	
		while(iterate.hasNext() ) {
		    Vector x = iterate.next();
		    Vector y = iterate.next();
		    if(x.type.equals("E") && y.type.equals("E")){
		    	System.out.println("NO!");
		    	return;
		    }
		    
		  }
		System.out.println("NO CONCURRENT CRITICAL SECTIONS!");
    }
}
