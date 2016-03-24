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
		boolean lock = false;
		boolean fail = false;
		boolean inquire = false;
		Logger.log(myHost,"Got request from " + incomingMessage.getPID());
		synchronized(Process.ss){
			if(Process.ss.lockingRequest == null){
				Process.ss.lockingRequest = incomingMessage;
				lock = true;

			} 
			if(Process.ss.lockingRequest != null && Process.ss.lockingRequest.getClock() < incomingMessage.getClock()){
				Process.ss.waitingQueue.add(incomingMessage);
				fail = true;

			}
			if(Process.ss.lockingRequest != null && Process.ss.lockingRequest.getClock() >= incomingMessage.getClock() && Process.ss.lockingRequest.getPID() > incomingMessage.getPID()){
				inquire = true;
				Process.ss.waitingQueue.add(incomingMessage);

			}
		}
		Logger.log(myHost,"INGA VANDACHEYY!!");
		if(lock){
			sendLock(incomingMessage.getPID());
			return;
		}
		if(fail){
			sendFail(incomingMessage.getPID());
			return;
		}
		if(inquire){
			sendInquire(Process.ss.lockingRequest.getPID());
			return;
		}
	}
	public void onReceiveRelease(Message incomingMessage){
		Logger.log(myHost,"Got release from " + incomingMessage.getPID());
		boolean lock = false;
		synchronized(Process.ss){
			if(Process.ss.waitingQueue.isEmpty()){
				Process.ss.lockingRequest = null;
			}

			else{
				String line = "My Wait Queue:- ";
				//			 Iterator<Message> through = Process.ss.waitingQueue.iterator() ;
				//		        while(through.hasNext() ) {
				//		                  line += through.next() + " " ;
				//		                }
				//			Logger.log(myHost, line);

				lock = true;
				Process.ss.lockingRequest = Process.ss.waitingQueue.remove();



			}
		}

		if(lock){
			sendLock(Process.ss.lockingRequest.getPID());
		}

	}

	public void onReceiveYield(Message incomingMessage){
		Logger.log(myHost,"Got yield from " + incomingMessage.getPID());
		boolean lock = false;
		synchronized(Process.ss){  
			if(!Process.ss.waitingQueue.isEmpty()){
				Process.ss.waitingQueue.add(Process.ss.lockingRequest);	   
				Process.ss.lockingRequest = Process.ss.waitingQueue.remove();
				lock = true;
			}
			else{
				Process.ss.lockingRequest = null;
			}
		}
		if(lock){
			sendLock(Process.ss.lockingRequest.getPID());
		}
	}
	public void sendLock(int pid){
		Logger.log(myHost,"Sending lock to " + pid);
		Process.clock.incrClock();
		PrintWriter currentWriter = myServer.mapNodeWriter.get(pid);
		synchronized(currentWriter){
			currentWriter.println("LOCK~" + myHost.getMe().getPID() + "~" + Process.clock.getValue());
			currentWriter.flush();
		}

	}

	public  void sendFail(int pid){
		Logger.log(myHost,"Sending fail to " + pid);
		Process.clock.incrClock();
		PrintWriter currentWriter = myServer.mapNodeWriter.get(pid);
		synchronized(currentWriter){
			currentWriter.println("FAIL~" + myHost.getMe().getPID() + "~" + Process.clock.getValue());
			currentWriter.flush();
		}
	}
	public void sendInquire(int pid){
		Logger.log(myHost,"Sending inquire to " + pid);
		Process.clock.incrClock();
		PrintWriter currentWriter = myServer.mapNodeWriter.get(pid);
		synchronized(currentWriter){
			currentWriter.println("INQUIRE~" + myHost.getMe().getPID() + "~" + Process.clock.getValue());
			currentWriter.flush();
		}
	}

	public  void run(){
		try{
			Logger.log(myHost, "Starting server thread @ " + myHost.getMe().getPID());
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			myServer.mapNodeWriter.put(incomingPID, writer);
			while(true){



				message = reader.readLine();
				String []tokens = message.split("[~]");
				Process.clock.updateClock(Integer.parseInt(tokens[2]));
				Message incomingMessage  = new Message(Integer.parseInt(tokens[2]),Integer.parseInt(tokens[1]),tokens[0]);


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