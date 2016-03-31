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
	public static boolean inCS = false;
	public static HashMap<Integer,Boolean> whoInquired = new HashMap<Integer,Boolean>();
	boolean isConnectionEstablished(){
		return hostPing;
	}
	public synchronized static void setInCS(boolean value){
		 inCS = value;
	}
	String role;
	public static HashMap<Integer,Boolean> locks = new HashMap<Integer,Boolean>();
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
	    
		if(!inCS && !whoInquired.isEmpty()){
			//send yield to all those quorum members who sent you inquired
			for(Integer i:whoInquired.keySet() ){
				sendYield(i.intValue());
				
				}
			whoInquired.clear();
		}

	}
	public synchronized static void onCsExit(){
		Process.cs.inCS = false;
	    ClientRequest.receivedFail = false;
//		ClientRequest.lockingCount = 0;
//		TCPClient.lockingCount = 0;
		ClientRequestV2.locks.clear();
		ClientRequestV2.setInCS(false);
		whoInquired.clear();
		Clock.incrClock();
		Process.sendingClock = Clock.getVectorClock();
	for(int i = 0; i < myClient.clientRequests.size(); i++){
		   int quorumPID = myClient.clientRequests.get(i).quorumMember.getPID();
		    ClientRequestV2.sendRelease(quorumPID);
	}
	Logger.log(myHost, "Sent Release message");
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
		if(inCS){
			return false;
		}
		if(hasFailed()){
		lockingCount--;
		myClient.lockingCount = lockingCount;
		}
		else{
			//we are not in cs but not failed, so we keep track.
			whoInquired.put(incomingMessage.getPID(), true);
		}
		return receivedFail;
	}


	public synchronized static void onLocked(Message incomingMessage){
//		synchronized(Process.cs){
//			Process.cs.locks.put(quorumMember.getPID(),true);
//		}
		lockingCount++;
		locks.put(incomingMessage.getPID(), true);
		//myClient.lockingCount = lockingCount;
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
	public synchronized static void sendYield(int quorumPID){
		Logger.log(myHost,"Sending yield to " + quorumPID);
		locks.remove(quorumPID);
		Clock.incrClock();
		PrintWriter writer = writerMap.get(quorumPID);
		writer.println("YIELD~" +myHost.getMe().getPID() + "~" + Clock.getVectorClock());
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
		
		PrintWriter writer = writerMap.get(quorumPID);
		writer.println("RELEASE~" +myHost.getMe().getPID() + "~" + Process.sendingClock);
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
				//e.printStackTrace();
			}
		
		}
		
		sendRequest(quorumMember.getPID());
		
		while(true){
			try {

				String  message = reader.readLine();
				String tokens[] = message.split("[~]");
				Clock.updateVectorClock(Clock.readVector(tokens));
				Logger.log(myHost,"Mesage came :- " + message);
				Message incomingMessage =  new Message(Clock.returnClockValue(tokens, Integer.parseInt(tokens[1])),Integer.parseInt(tokens[1]),tokens[0]);
				messageQueue.add(incomingMessage);

			} catch (IOException e) {
			    e.printStackTrace();	
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
					e.printStackTrace();
				}
			 }
		}


	}
}
