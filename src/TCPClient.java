import java.util.ArrayList;

public class TCPClient implements Runnable{
	private int pid;
	public TCPServer myServer;
	public Host myHost;
	public ArrayList<ClientSock> connections = new ArrayList<ClientSock>();
	public void setPID(int id){
		pid = id;
	}
	boolean threadForked = false;
	//condition to check the
	public synchronized boolean hasLockedAll(){
		if(connections.size() < myHost.numberOfQuorumMembers){
			return false;
		}
		for(int i = 0; i < connections.size(); i++){
			if(connections.get(i).isLocked() == false){
				return false;
			}
		}
		return true;
	}
	
	public void onCsEnter(){

		if(!threadForked){
			Thread t = new Thread(this);
			System.out.println("Atleast I reached here");
			t.start();
			System.out.println("Requesting for criticle section");

			threadForked = true;
		}
		while(!hasLockedAll()); 
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

		}

		System.out.println("#Connections : " + connections.size() + " #QuorumNum : " + myHost.numberOfQuorumMembers);
	}
}