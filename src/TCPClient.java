import java.util.ArrayList;

public class TCPClient implements Runnable{
	private int pid;
	public TCPServer myServer;
	public Host myHost;
	public ArrayList<ClientSock> connections = new ArrayList<ClientSock>();
	public void setPID(int id){
		pid = id;
	}
	public void onCsEnter(){
	       for(i = 0; i < myHost.numberOfQuorumMembers; i++){
	    	   connections.get(i).sendRequest(0 /*request id*/);
	       }
	       
	       while(!lockedAll());
	}
	
	
	public void criticalSection() throws Exception{
		Thread.sleep(Config.getCsTime());
	}
	public void setData(TCPServer server,Host h){
		myServer = server;
		myHost = h;
	}
	public void run(){
		
		for(int i = 0; i < myHost.numberOfQuorumMembers; i++ ){
        	ClientSock c = new ClientSock(this,myServer,myHost.quorumByID(myHost.quorumList.get(i)),myHost);
        	connections.add(c);
        	Thread t = new Thread(c);
        	t.start();
        	try{
            t.join();
        	}
        	catch(Exception e){
        		
        	}
        }
	}
}