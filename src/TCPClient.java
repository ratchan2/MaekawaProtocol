import java.util.ArrayList;
import java.util.*;
public class TCPClient implements Runnable{
	private int pid;
	public TCPServer myServer;
	public Host myHost;
	public ArrayList<ClientSock> connections = new ArrayList<ClientSock>();
	public void setPID(int id){
		pid = id;
	}

	

	public synchronized boolean hasLockedAll(){
		if(connections.size() < myHost.numberOfQuorumMembers){
			return false;
		}
		for(int i = 0; i < connections.size(); i++){
			if(Process.cs.locks.get(connections.get(i).quorumMember.getPID()) == false){
				return false;
			}
		}
		return true;
	}
	public synchronized boolean hasReceivedFail(){
		for(int i = 0; i < connections.size(); i++){
			if(Process.cs.fails.get(connections.get(i).quorumMember.getPID())){
				return true;
			}
		}
		return false;
	}

	public synchronized void onCsEnter(){

		for(int i = 0; i < myHost.numberOfQuorumMembers; i++ ){
		
			Process.cs.locks.put(myHost.quorumList.get(i),false);
			Process.cs.fails.put(myHost.quorumList.get(i),false);
			Thread t = new Thread(connections.get(i));
			t.start();

		}
		
		while(!hasLockedAll());
		Process.cs.inCS = true;
	}


	public void setData(TCPServer server,Host h){
		myServer = server;
		myHost = h;
	}
	public void run(){
		String line = "My Quorum members:- ";
		for(int i = 0; i < myHost.quorumList.size(); i++){
			line += myHost.quorumList.get(i) + " ";
		}
		Logger.log(myHost, line);
		for(int i = 0; i < myHost.numberOfQuorumMembers; i++ ){
			ClientSock c = new ClientSock(this,myServer,myHost.quorumByID(myHost.quorumList.get(i)),myHost);
			Process.cs.locks.put(myHost.quorumList.get(i),false);
			Process.cs.fails.put(myHost.quorumList.get(i),false);
			connections.add(c);
			Thread t = new Thread(c);
			t.start();

		}
	}
}