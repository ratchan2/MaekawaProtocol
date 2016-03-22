import java.net.InetAddress;
import java.util.HashMap;
import java.util.*;
public class Host{

	private String myname;
	private int me;
	private HashMap<Integer,Node> quorum = new HashMap<Integer,Node>();
	public int numberOfQuorumMembers;
	public ArrayList<Integer> quorumList = new ArrayList<Integer>();
	public Node myNode;
	public HashMap<String,Integer> hostMap = new HashMap<String,Integer>();
	public void readFile(String filename) throws Exception{}
    public Host(int pid,Node node)throws Exception{
    	myname = InetAddress.getLocalHost().getHostName();
    	me = pid; 	
    	myNode = node;
    	numberOfQuorumMembers = 0;
    }
    public String whoami(){
    	return myname;
    }
    public void addQuorumMember(int pid, Node node){
    	quorum.put(pid, node);
    	quorumList.add(pid);
    	numberOfQuorumMembers++;
    }
    public Node quorumByID(int pid){
    	return quorum.get(pid);
    	
    }
    
    public Node getMe(){
    	return myNode;
    }

}

class Node{
	private String hostname;
	private int port;
	private int pid;
	public String getHostName(){
		return hostname;
	}
	public int getPort(){
		return port;
	}
	public int getPID(){
		return pid;
	}
	public Node(String h, int p, int pid){
		hostname = h;
		port = p;
		this.pid = pid;
	}
	
}