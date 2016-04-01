import java.io.BufferedReader;

import java.util.concurrent.*;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;
public class ServerSockV2 implements Runnable{
	Socket socket;
	TCPServer myServer;
	public static Host myHost;
	String message;
	BufferedReader reader;
	PrintWriter writer;
	String incomingHost;
	int incomingPID;
	public static LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message> ();
	public static PriorityQueue<Message> waitingQueue = new PriorityQueue<Message>(Config.getNumberOfNodes()*Config.getNumberOfRequests(), new MessageComparator());
	public static Map<Integer, PrintWriter> mapNodeWriter = new HashMap<Integer,PrintWriter>();
	public static Message lockingRequest = null;
	public static boolean haveInquired = false;
	public String role;
	public void setRole(String r){
		role = r;
		
	}
	public ServerSockV2(Socket s,TCPServer server, Host h){
		socket = s;
		myServer = server;
		myHost = h;
		if(socket != null){
		incomingHost = socket.getInetAddress().getHostName().split("[.]")[0];
		incomingPID = myHost.hostMap.get(incomingHost);
		}
		
	}

	public void printMyQueue(){
        if(lockingRequest != null){
		Logger.log(myHost, "My locking request:- " + lockingRequest.toString());
        }
		String line = "My Wait Queue:- ";
		Iterator<Message> through = waitingQueue.iterator() ;
		while(through.hasNext() ) {
			line += through.next() + " " ;
		}
		Logger.log(myHost, line);

	}
	public static void onReceiveRequest(Message incomingMessage){
		boolean lock = false;
		boolean fail = false;
		boolean inquire = false;
		Logger.log(myHost,"Got request from " + incomingMessage.getPID());

		if(lockingRequest == null){
			lockingRequest = incomingMessage;
			lock = true;
			sendLock(incomingMessage.getPID());
			incomingMessage = null;
			
		} 

		if((lockingRequest != null && incomingMessage != null && lockingRequest.getClock() < incomingMessage.getClock())
				|| (lockingRequest != null && incomingMessage != null && lockingRequest.getClock() == incomingMessage.getClock() && lockingRequest.getPID() < incomingMessage.getPID())
				|| ( !waitingQueue.isEmpty() && incomingMessage != null && waitingQueue.peek().getClock() < incomingMessage.getClock())
				|| (!waitingQueue.isEmpty() && incomingMessage != null && waitingQueue.peek().getClock() == incomingMessage.getClock() && waitingQueue.peek().getPID() < incomingMessage.getPID())){
			    
			waitingQueue.add(incomingMessage);
			fail = true;
			sendFail(incomingMessage.getPID());
			
			incomingMessage = null;

		}
		if((lockingRequest != null && incomingMessage != null && lockingRequest.getClock() == incomingMessage.getClock() && lockingRequest.getPID() > incomingMessage.getPID())
				||( lockingRequest != null && incomingMessage != null && lockingRequest.getClock() > incomingMessage.getClock()) ){
			inquire = true;
			waitingQueue.add(incomingMessage);
			if(!haveInquired){
			sendInquire(lockingRequest.getPID());
			}
			incomingMessage = null;

		}


		if(lock){
			return;
		}
		if(fail){

			return;
		}
		if(inquire){

			return;
		}
	}
	public static void onReceiveRelease(Message incomingMessage){
		Logger.log(myHost,"Got release from " + incomingMessage.getPID());
	    haveInquired  = false;
		boolean lock = false;
			Logger.log(myHost, "Entered  receive ` block");
			if(waitingQueue.isEmpty()){
				lockingRequest = null;	
			}
			else{
				lock = true;
				lockingRequest = waitingQueue.remove();
				sendLock(lockingRequest.getPID());
			}
		
		if(lock){
			
		}

	}

	public static void onReceiveYield(Message incomingMessage){
		Logger.log(myHost,"Got yield from " + incomingMessage.getPID());
		boolean lock = false;
//		if(lockingRequest != null && lockingRequest.getPID() != incomingMessage.getPID()){
//			return;
//		}
		if(lockingRequest != null && incomingMessage.getPID() != lockingRequest.getPID() || (lockingRequest != null && incomingMessage.getPID() == lockingRequest.getPID() && incomingMessage.getClock() < lockingRequest.getClock())){
			return;
		}
		    haveInquired = false;
			if(!waitingQueue.isEmpty()){
				
				waitingQueue.add(lockingRequest);	
				lockingRequest = waitingQueue.remove();
				sendLock(lockingRequest.getPID());

			}
	

		

		
			
			

	}
	public static void sendLock(int pid){
		Logger.log(myHost,"Sending lock to " + pid);
		Clock.incrClock();
		PrintWriter currentWriter = mapNodeWriter.get(pid);
		currentWriter.println("LOCK~" + myHost.getMe().getPID() + "~" + Clock.getVectorClock());
		currentWriter.flush();


	}

	public static void sendFail(int pid){
		Logger.log(myHost,"Sending fail to " + pid);
		Clock.incrClock();
		PrintWriter currentWriter = mapNodeWriter.get(pid);
		currentWriter.println("FAIL~" + myHost.getMe().getPID() + "~" + Clock.getVectorClock());
		currentWriter.flush();

	}
	public static void sendInquire(int pid){
		Logger.log(myHost,"Sending inquire to " + pid);
		Clock.incrClock();
		PrintWriter currentWriter = mapNodeWriter.get(pid);
		currentWriter.println("INQUIRE~" + myHost.getMe().getPID() + "~" + Clock.getVectorClock());
		currentWriter.flush();

	}

	public void run(){
		if(role.equals("PRODUCER")){
			try{
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
				mapNodeWriter.put(incomingPID, writer);
				while(true){

					message = reader.readLine();
					String []tokens = message.split("[~]");
					Clock.updateVectorClock(Clock.readVector(tokens));
					Message incomingMessage  = new Message(Clock.returnClockValue(tokens, Integer.parseInt(tokens[1])),Integer.parseInt(tokens[1]),tokens[0]);
					Logger.log(myHost, incomingMessage.toString());
						messageQueue.add(incomingMessage);
				
					
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}

		}
		else{
			while(true){	
				Message currentMessage = null;
				try {
					currentMessage = messageQueue.take();
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(currentMessage != null && currentMessage.getMessageType().equals("REQUEST")){

					onReceiveRequest(currentMessage);
					printMyQueue();


				}
				if(currentMessage != null && currentMessage.getMessageType().equals("RELEASE")){

					onReceiveRelease(currentMessage);
					printMyQueue();

				}

				if( currentMessage != null && currentMessage.getMessageType().equals("YIELD")){

					onReceiveYield(currentMessage);
					printMyQueue();

				}
			}
		}
	}
}
