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
	public void onReceiveRequest(Message message){
		sendLock();
	}
	public void onReceiveFail(Message message){
		
	}
	
	public void onReceiveYield(Message message){
		
	}
	public void sendLock(){
		Clock.incrClock();
		writer.println("LOCK~" + myHost.getMe().getPID() + "~" + Clock.getValue());
		writer.flush();
	}
	public void run(){
		 try{
			 System.out.println("Starting server thread @ " + myHost.getMe().getPID());
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream());
	   	while(true){
	   	    
	   	    	
	   	    	
			    message = reader.readLine();
			    System.out.println("Got this message: " + message);
			  
			    if(message.contains("REQUEST")){
			    	System.out.println("ulla iruken");
			    	/*Scanner sc = new Scanner(message);
			    	sc.next();
			    	int pid = sc.nextInt();
			    	int incomingClock = sc.nextInt();
			    	
			    	onReceiveRequest(new Message(sc.nextInt(),incomingPID,"REQUEST"));*/
			    	sendLock();
			    	continue;
			    }
			    if(message.indexOf("RELEASE") != -1){
			    	
			    	onReceiveFail(new Message(-1, incomingPID,"RELEASE"));
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