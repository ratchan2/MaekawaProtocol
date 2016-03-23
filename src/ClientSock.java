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
      boolean isConnectionEstablished(){
    	  return hostPing;
      }
      public void onFail(){
    	  //I have received a fail message
    	  receivedFail = true;
      }
      public void onInquire(){
    	  
      }
      
      public synchronized void onLocked(){
    	  locked = true;
    	  System.out.println("REceived lock from  " + quorumMember.getPID());
//    	  boolean result = true;
//    	  for(int i = 0; i < myClient.connections.size(); i++){
//    		 //TODO: Check if isLocked is returning the latest value
//    		result = result && myClient.connections.get(i).isLocked();
//    			 //int quorumLockedRecieved = quorumLockedRecieved+1 
//    	  }
//    	  if(result == true){
//    		   myClient.canEnterCS = true;
//               myClient.notify();       		  
//    	  }
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
      public void sendRequest(){
    	  
    	   System.out.println("SENDING REQUEST to " +  quorumMember.getPID());
    	   Clock.incrClock();
    	   try{
    	   System.out.println(socket.getOutputStream());
    	   }
    	   catch(Exception e){
    		   System.out.println("Exception with " + quorumMember.getHostName());
    	   }
    	   //PrintWriter writer = new PrintWriter(socket.getOutputStream());
    	   try{
    	  // BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    	   PrintWriter writer = new PrintWriter(socket.getOutputStream());
		  // writer.write("REQUEST~"  +"~" + Clock.getValue());
    	   writer.println("REQUEST~" +myHost.getMe().getPID() + "~" + Clock.getValue());
    	   writer.flush();
    	   }
    	   catch(Exception e){
    		   System.out.println("Exception2 with " + quorumMember.getHostName());
    	   }
      }
      public void run() {
    
    	  while(!hostPing){
    		  try{
    			 
    			  socket = new Socket(quorumMember.getHostName()+ ".utdallas.edu",quorumMember.getPort());
    			  
    			  hostPing = true;
    			 
    		  }
    		  catch(Exception e){
    		
    		  }
    	  }
    	
    	  try {
			sendRequest();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	  BufferedReader reader = null;
		try {
			  reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	  while(true){
    		 try {
    			
				String  message = reader.readLine();
				String [] tokens = message.split("[~]");
				Clock.updateClock(Integer.parseInt(tokens[2]));
				System.out.println("Mesage came :- " + message);
				
				if(tokens[0].equals("LOCK")){
	    			   onLocked();
	    		 }
	    		 if(tokens[0].equals("INQUIRE")){
	    			   onInquire();
	    		 }
	    		 if(tokens[0].equals("FAIL")){
	    			 onFail();
	    		 }
	    		 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		 
    		
    	  }
    	  
    	  
    	  
    	  
      }
}
