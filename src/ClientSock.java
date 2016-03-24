import java.io.BufferedReader;
import java.io.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;


public class ClientSock  implements Runnable{
	TCPClient myClient;
	TCPServer myServer;
	public Socket socket;
	public Node quorumMember;

	// BufferedReader reader;
	Host myHost;
	public  boolean hostPing = false;
	boolean receivedFail = false;
	PrintWriter writer = null;
	BufferedReader reader = null;
	boolean locked = false;
	boolean isConnectionEstablished(){
		return hostPing;
	}
	public  void onFail(Message incomingMessage){
		//I have received a fail message
		synchronized(Process.cs){
			Process.cs.fails.put(quorumMember.getPID(), true);
		}

	}
	public  void onInquire(Message incomingMessage){
		synchronized(Process.cs){
			if(!Process.cs.inCS && myClient.hasReceivedFail()){
				synchronized(Process.cs.locks){
					Process.cs.locks.put(quorumMember.getPID(), false);

				}
			}
		}
		sendYield();
	}

	public void onLocked(Message incomingMessage){
		synchronized(Process.cs){
			Process.cs.locks.put(quorumMember.getPID(),true);
		}
		Logger.log(myHost,"Received lock from  " + quorumMember.getPID());

	}
	public  boolean isLocked(){
		return locked;
	}
	public ClientSock(TCPClient c, TCPServer server, Node q,Host h){
		myClient = c;
		myServer = server;
		quorumMember  = q;
		myHost = h;

	}
	public  void sendYield(){
		Logger.log(myHost,"Sending yield to " + quorumMember.getPID());
		Process.clock.incrClock();
		writer.println("YIELD~" +myHost.getMe().getPID() + "~" + Process.clock.getValue());
		writer.flush();
	}
	public  void sendRequest(){

		Logger.log(myHost,"Sending request to " +  quorumMember.getPID());
		Process.clock.incrClock();
		writer.println("REQUEST~" +myHost.getMe().getPID() + "~" + Process.clock.getValue());
		writer.flush();

	}
	public  void sendRelease(){
		Logger.log(myHost,"Sending release to " + quorumMember.getPID());
		Process.clock.incrClock();
		writer.println("RELEASE~" +myHost.getMe().getPID() + "~" + Process.clock.getValue());
		writer.flush();

	}
	public synchronized void run() {
		Logger.log(myHost, "Started client thread w.r.t " + quorumMember.getPID());
		while(!hostPing){
			try{

				socket = new Socket(quorumMember.getHostName()+ ".utdallas.edu",quorumMember.getPort());
				writer = new PrintWriter(socket.getOutputStream());	
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				hostPing = true;
				return;
			}
			catch(Exception e){

			}
		}

		sendRequest();


		while(true){
			try {

				String  message = reader.readLine();
				String [] tokens = message.split("[~]");
				Process.clock.updateClock(Integer.parseInt(tokens[2]));
				Logger.log(myHost,"Mesage came :- " + message);
				Message incomingMessage =  new Message(Integer.parseInt(tokens[2]),Integer.parseInt(tokens[1]),tokens[0]);
				if(tokens[0].equals("LOCK")){
					onLocked(incomingMessage);
					continue;
				}
				if(tokens[0].equals("INQUIRE")){
					onInquire(incomingMessage);
					continue;
				}
				if(tokens[0].equals("FAIL")){
					onFail(incomingMessage);
					continue;
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}




	}
}
