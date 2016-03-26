import java.io.BufferedReader;
import java.io.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
public class ClientRequestV2 implements Runnable{
	public static TCPClient myClient;
	TCPServer myServer;
	public Socket socket;
	public Node quorumMember = null;
	
	// BufferedReader reader;
	public static Host myHost;
	public  boolean hostPing = false;
	PrintWriter writer = null;
	BufferedReader reader = null;
	boolean locked = false;
	public boolean signal = false;
	boolean isConnectionEstablished(){
		return hostPing;
	}
	String role;
	public void setRole(String r){
		role = r;
	}
	public static LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();
	public static int lockingCount = 0;
	public static boolean receivedFail = false;
	public static HashMap<Integer,PrintWriter> writerMap = new HashMap<Integer,PrintWriter>();
	
	public synchronized static void onFail(Message incomingMessage){
		//I have received a fail message
//		synchronized(Process.cs){
//			Process.cs.fails.put(quorumMember.getPID(), true);
//		}
		receivedFail = true;

	}
	public synchronized static boolean hasFailed(){
		return receivedFail;
	}
	public synchronized static boolean onInquire(Message incomingMessage){
//		synchronized(Process.cs){
//			if(!Process.cs.inCS && myClient.hasReceivedFail()){
//				synchronized(Process.cs.locks){
//					Process.cs.locks.put(quorumMember.getPID(), false);
//
//				}
//			}
//		}
		if(Process.cs.inCS){
			return false;
		}
		if(hasFailed()){
		lockingCount--;
		myClient.lockingCount = lockingCount;
		}
		return receivedFail;
	}


	public synchronized void onLocked(Message incomingMessage){
//		synchronized(Process.cs){
//			Process.cs.locks.put(quorumMember.getPID(),true);
//		}
		lockingCount++;
		myClient.lockingCount = lockingCount;
		Logger.log(myHost,"Received lock from  " + incomingMessage.getPID() + ",my locking count :" + lockingCount);
		

	}
	public  boolean isLocked(){
		return locked;
	}
	public ClientRequestV2(TCPClient c, TCPServer server, Node q,Host h,String r){
		myClient = c;
		myServer = server;
		if(q != null){
		quorumMember  = q;
		}
		myHost = h;
		role = r;

	}
	public static void sendYield(int quorumPID){
		Logger.log(myHost,"Sending yield to " + quorumPID);
		Clock.incrClock();
		PrintWriter writer = writerMap.get(quorumPID);
		writer.println("YIELD~" +myHost.getMe().getPID() + "~" + Clock.getValue());
		writer.flush();
	}
	public  static void sendRequest(int quorumPID){

		Logger.log(myHost,"Sending request to " +  quorumPID);
		//Clock.incrClock();
		PrintWriter writer = writerMap.get(quorumPID);
		writer.println("REQUEST~" +myHost.getMe().getPID() + "~" + Process.sendingClock);
		writer.flush();

	}
	public  static void sendRelease(int quorumPID){
		Logger.log(myHost,"Sending release to " + quorumPID);
		Clock.incrClock();
		PrintWriter writer = writerMap.get(quorumPID);
		writer.println("RELEASE~" +myHost.getMe().getPID() + "~" + Clock.getValue());
		writer.flush();

	}
	public void run() {
		
		Logger.log(myHost,"Gonna wait now");
		if(role.equals("PRODUCER")){
		while(!signal){

			try{

				socket = new Socket(quorumMember.getHostName()+ ".utdallas.edu",quorumMember.getPort());
				writerMap.put(quorumMember.getPID(),new PrintWriter(socket.getOutputStream()));	
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				signal = true;
				
			}
			catch(Exception e){

			}
		
		}
		
		sendRequest(quorumMember.getPID());
		
		while(true){
			try {

				String  message = reader.readLine();
				String tokens[] = message.split("[~]");
				Clock.updateClock(Integer.parseInt(tokens[2]));
				Logger.log(myHost,"Mesage came :- " + message);
				Message incomingMessage =  new Message(Integer.parseInt(tokens[2]),Integer.parseInt(tokens[1]),tokens[0]);
				messageQueue.add(incomingMessage);

			} catch (IOException e) {
				
			}


		}

		}
		else{
			 while(true){
				 try {
					Message currentMessage = messageQueue.take();
					if(currentMessage.getMessageType().equals("LOCK")){
						onLocked(currentMessage);
						continue;
					}
					if(currentMessage.getMessageType().equals("INQUIRE")){
						if(onInquire(currentMessage)){
							sendYield(currentMessage.getPID());
						}
						continue;
					}
					if(currentMessage.getMessageType().equals("FAIL")){
						onFail(currentMessage);
						continue;
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					
				}
			 }
		}


	}
}
