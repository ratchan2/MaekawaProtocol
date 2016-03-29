import java.io.BufferedReader;
import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;



public class TCPServer implements Runnable{
	public TCPClient myClient;
	public Host myHost;
	
	public void run(){
		try
		{
			//this is java handling tcp socket
			ServerSocket serverSock = new ServerSocket(myHost.getMe().getPort());
			//seversockv2 membership set handler as our process is of structure 
//			P(Quorum--> our client will interact with it, Membership---> our server will have membership.size thread and one thread) 
			ServerSockV2 consumer = new ServerSockV2(null,this,myHost);
			consumer.setRole("CONSUMER");
			Thread consumerThread = new Thread(consumer);
			consumerThread.start();
			while(true){
			
				Socket sock = serverSock.accept();
			 	ServerSockV2 currentSock = new ServerSockV2(sock,this,myHost);
			 	currentSock.setRole("PRODUCER");
				Thread t = new Thread(currentSock);
			    t.start();
			    	
			}
		   
      }
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	
	}
	public void setData(TCPClient client,Host h){
		myClient = client;
		myHost = h;
	}
	
}
