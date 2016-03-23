import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;
public class ServerSock implements Runnable{
	Socket socket;
	TCPServer myServer;
	Host myHost;
	String message;
	BufferedReader reader;
	PrintWriter writer;
	String incomingHost;
	int incomingPID;



	public ServerSock(Socket s,TCPServer server, Host h){
		socket = s;
		myServer = server;
		myHost = h;
		incomingHost = socket.getInetAddress().getHostName().split("[.]")[0];
		incomingPID = myHost.hostMap.get(incomingHost);
	}
	public void onReceiveRequest(Message incomingMessage){
		if(myServer.lockingRequest == null){
			myServer.lockingRequest = incomingMessage;
			sendLock(incomingMessage.getPID());

		} 
		if(myServer.lockingRequest != null && myServer.lockingRequest.getClock() < incomingMessage.getClock()){
			myServer.waitingQueue.put(incomingMessage);
			sendFail(incomingMessage.getPID());
		}
		if(myServer.lockingRequest != null && myServer.lockingRequest.getClock() >= incomingMessage.getClock()){
			sendInquire(myServer.lockingRequest.getPID());
			myServer.waitingQueue.put(incomingMessage);

		}
	}
	public void onReceiveRelease(Message incomingMessage){
		if(myServer.waitingQueue.isEmpty()){
			myServer.lockingRequest = null;
		}
		else{
			try {
				myServer.lockingRequest = myServer.waitingQueue.take();
			    
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendLock(myServer.lockingRequest.getPID());
		}
	}

	public void onReceiveYield(Message message){
           if(!myServer.waitingQueue.isEmpty()){
        	   try{
        	   myServer.waitingQueue.put(myServer.lockingRequest);	   
        	   myServer.lockingRequest = myServer.waitingQueue.take();
        	   sendLock(myServer.lockingRequest.getPID());
        	   }
        	   catch(Exception e){
        		   
        	   }
           }
           else{
        	   myServer.lockingRequest = null;
           }
	}
	public synchronized void sendLock(int pid){
		Clock.incrClock();
		PrintWriter currentWriter = myServer.mapNodeWriter.get(pid);
		currentWriter.println("LOCK~" + myHost.getMe().getPID() + "~" + Clock.getValue());
		currentWriter.flush();
		myServer.sentLocked = true;
	}
	
	public synchronized void sendFail(int pid){
		Clock.incrClock();
		PrintWriter currentWriter = myServer.mapNodeWriter.get(pid);
		currentWriter.println("FAIL~" + myHost.getMe().getPID() + "~" + Clock.getValue());
		currentWriter.flush();

	}
	public synchronized void sendInquire(int pid){
		Clock.incrClock();
		PrintWriter currentWriter = myServer.mapNodeWriter.get(pid);
		currentWriter.println("INQUIRE~" + myHost.getMe().getPID() + "~" + Clock.getValue());
		currentWriter.flush();
	}

	public synchronized void run(){
		try{
			System.out.println("Starting server thread @ " + myHost.getMe().getPID());
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			myServer.mapNodeWriter.put(incomingPID, writer);
			while(true){



				message = reader.readLine();
				String []tokens = message.split("[~]");
				Clock.updateClock(Integer.parseInt(tokens[2]));
				Message incomingMessage  = new Message(Integer.parseInt(tokens[2]),Integer.parseInt(tokens[1]),tokens[0]);
				System.out.println("Got this message: " + message);


				if(message.contains("REQUEST")){

					onReceiveRequest(incomingMessage);
					continue;

				}
				if(message.contains("RELEASE")){

					onReceiveRelease(incomingMessage);
					continue;
				}

				if(message.indexOf("YIELD") != -1){

					onReceiveYield(new Message(-1, incomingPID,"YIELD"));
					continue;
				}

			}
		}
		catch(Exception e){

		}

	}
}