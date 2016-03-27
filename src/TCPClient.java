import java.util.ArrayList;
import java.util.*;
public class TCPClient implements Runnable{
	private int pid;
	public TCPServer myServer;
	public Host myHost;
	public ArrayList<HostPing> connections = new ArrayList<HostPing>();
	public ArrayList<ClientRequestV2> clientRequests = new ArrayList<ClientRequestV2>();
	public void setPID(int id){
		pid = id;
	}

	public boolean threadForked = false;
	public static int lockingCount = 0;
	public static int quorumSize = Process.cs.quorumSize;
    public TCPClient(Host h, TCPServer s){
    	ClientRequestV2 consumer = new ClientRequestV2(this,myServer,null,myHost,"CONSUMER");
    	Thread consumerThread = new Thread(consumer);
    	consumerThread.start();
    	myHost = h;
    	myServer = s;
    	for(int i = 0; i < myHost.quorumList.size(); i++){
    		clientRequests.add(new ClientRequestV2(this,myServer,myHost.quorumByID(myHost.quorumList.get(i)),myHost,"PRODUCER"));
    	}
    }
	public synchronized static boolean hasLockedAll(){
		if(ClientRequestV2.locks.size() < quorumSize){
			return false;
		}
		return true;
	}
	public  boolean hasReceivedFail(){
//		for(int i = 0; i < connections.size(); i++){
//			if(Process.cs.fails.get(connections.get(i).quorumMember.getPID())){
//				return true;
//			}
//		}
//		return false;
		return Process.cs.receivedFail;
	}

	public void onCsEnter(){
		Clock.incrClock();
		Process.sendingClock = Clock.getValue();
      for(int i = 0; i < myHost.numberOfQuorumMembers; i++ ){
//    	  Process.cs.locks.put(myHost.quorumList.get(i),false);
//		  Process.cs.fails.put(myHost.quorumList.get(i),false);
		  
    	  if(!threadForked){
    		Thread t = new Thread(clientRequests.get(i));
			t.start();
            
		}
    	  else{
    		   int quorumPID = clientRequests.get(i).quorumMember.getPID();
    		   ClientRequestV2.sendRequest(quorumPID);
    	  }
       }
      threadForked = true;
        
      while(!hasLockedAll());
      
      ClientRequestV2.setInCS(true);
        
	}


	public void setData(TCPServer server,Host h){
		myServer = server;
		myHost = h;
	}
	public void run(){
//		for(int i = 0; i < myHost.numberOfQuorumMembers; i++ ){
//			HostPing h = new HostPing(this,myServer,myHost.quorumByID(myHost.quorumList.get(i)),myHost,clientRequests.get(i));
//			Process.cs.locks.put(myHost.quorumList.get(i),false);
//			Process.cs.fails.put(myHost.quorumList.get(i),false);
//			connections.add(h);
//			Thread t = new Thread(h);
//			t.start();
//
//		}
	}
}