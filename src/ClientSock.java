import java.net.*;
public class ClientSock  implements Runnable{
      TCPClient myClient;
      TCPServer myServer;
      Socket socket;
      Node quorumMember;
      Host myHost;
      boolean hostPing = false;
      public ClientSock(TCPClient c, TCPServer server, Node q,Host h){
    	  myClient = c;
    	  myServer = server;
    	  quorumMember  = q;
    	  myHost = h;
    	
      }
      public void run() { 
    	  while(!hostPing){
    		  try{
    			 
    			  socket = new Socket(quorumMember.getHostName() + ".utdallas.edu",quorumMember.getPort());
    			  hostPing = true;
    		  }
    		  catch(Exception e){
    			
    		  }
    		  
    	  }
	  }
}
