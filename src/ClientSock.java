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
      Node quorumMember;
      
     // BufferedReader reader;
      Host myHost;
      public  boolean hostPing = false;
      boolean receivedFail = false;
      boolean locked = false;
      PrintWriter writer = null;
      BufferedReader reader = null;
      boolean isConnectionEstablished(){
    	  return hostPing;
      }
      public synchronized void onFail(Message incomingMessage){
    	  //I have received a fail message
    	  receivedFail = true;
      }
      public synchronized void onInquire(Message incomingMessage){
    	     if(!myClient.inCS && myClient.hasReceivedFail()){
    	    	   sendYield();
    	     }
      }
      
      public synchronized void onLocked(Message incomingMessage){
    	  locked = true;
    	  Logger.log(myHost,"Received lock from  " + quorumMember.getPID());

      }
      public boolean isLocked(){
    	  return locked;
      }
      public ClientSock(TCPClient c, TCPServer server, Node q,Host h){
    	  myClient = c;
    	  myServer = server;
    	  quorumMember  = q;
    	  myHost = h;
    	  
      }
      public void sendYield(){
    	  Logger.log(myHost,"Sending yield to " + quorumMember.getPID());
    	  Clock.incrClock();
    	  writer.println("YIELD~" +myHost.getMe().getPID() + "~" + Clock.getValue());
    	  writer.flush();
      }
      public  void sendRequest(){
    	  
    	   Logger.log(myHost,"Sending request to " +  quorumMember.getPID());
    	   Clock.incrClock();
    	   writer.println("REQUEST~" +myHost.getMe().getPID() + "~" + Clock.getValue());
    	   writer.flush();
    	 
      }
      public void sendRelease(){
    	  Logger.log(myHost,"Sending release to " + quorumMember.getPID());
    	  Clock.incrClock();
		  writer.println("RELEASE~" +myHost.getMe().getPID() + "~" + Clock.getValue());
    	  writer.flush();
    	   
      }
      public void run() {
    
    	  while(!hostPing){
    		  try{
    			 
    			  socket = new Socket(quorumMember.getHostName()+ ".utdallas.edu",quorumMember.getPort());
    			  writer = new PrintWriter(socket.getOutputStream());	
    			  reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    			  hostPing = true;
    			 
    		  }
    		  catch(Exception e){
    		
    		  }
    	  }
    	
    		sendRequest();
		
    	 
    	  while(true){
    		 try {
    			
				String  message = reader.readLine();
				String [] tokens = message.split("[~]");
				Clock.updateClock(Integer.parseInt(tokens[2]));
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
