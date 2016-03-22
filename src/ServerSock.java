import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;
public class ServerSock implements Runnable{
	Socket socket;
	TCPServer myServer;
	Host myHost;
	PrintWriter writer;
	String message;
	BufferedReader reader;
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
		
	}
	public void onReceiveFail(Message message){
		
	}
	
	public void onReceiveYield(Message message){
		
	}
	public void send(Message message){
		
	}
	public void run(){
	   	while(true){
	   	     try{
	   	    	reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			    message = reader.readLine();
			    if(message.indexOf("REQUEST") != -1){
			    	Scanner sc = new Scanner(message);
			    	sc.next();
			    	onReceiveRequest(new Message(sc.nextInt(),incomingPID,"REQUEST"));
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
	   	     catch(Exception e){
	   	    	 
	   	     }
	   	}
	}
}