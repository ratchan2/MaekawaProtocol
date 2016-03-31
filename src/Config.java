import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.*;
public class Config{
	private static int numberOfNodes;
	private static int requestDelay;
	private static int numberOfRequests;
	private static int csTime;
	private static int me; 
	public static void setMe(int pid){
		me = pid;
	}
	public static int getNumberOfNodes(){
		return numberOfNodes;
	}
	public static int getRequestDelay(){
		return requestDelay;
	}
	public static int getNumberOfRequests(){
		return numberOfRequests;
	}
	public static int getCsTime(){
		return csTime;
	}
	public static int getMe()
	{
		return me;
	}
	public static Host readFile(String filename)throws Exception{

		FileReader reader = new FileReader(filename);
		String line = null;
		BufferedReader file = new BufferedReader(reader);
		boolean paramsParsed = false;
		int hostsCount = 0;
		int currentHost = 0;
		HashMap<Integer,Node> hosts = new HashMap<Integer,Node>();
		HashMap<String,Integer> hostMap = new HashMap<String,Integer>();
		while((line = file.readLine()) != null){
		
			if(line.length() == 0 || (line.charAt(0) == '#')){
				continue;
			}
			Scanner sc = new Scanner(line);
			if(paramsParsed == false){
				 numberOfNodes = sc.nextInt();
				 requestDelay = sc.nextInt();
				 csTime  = sc.nextInt();
				 numberOfRequests = sc.nextInt();
				 paramsParsed = true;
				continue;
			}
		    if(hostsCount < numberOfNodes){
		    	int pid = sc.nextInt();
		    	String hostname = sc.next();
		    	 hosts.put(new Integer(pid), new Node(hostname,sc.nextInt(),pid));
		    	 hostMap.put(hostname, pid);
		    	 hostsCount++;
		    	 continue;
		    }
		    Host rHost = new Host(me,hosts.get(me));   
		    rHost.hostMap = hostMap;
		    if(hostsCount == numberOfNodes && currentHost < numberOfNodes){
		    	if(currentHost == me){ //honor only current node
		    		while(sc.hasNext()){
		    			int currentPID = sc.nextInt();
			    	    	rHost.addQuorumMember(currentPID, hosts.get(currentPID));
			    	}
		    		
		    		file.close();
		    		return rHost;
		    		
		    		
		    	}
		    	currentHost++;
		    	
		    }
		    
		}
	    return null;	
	}
}