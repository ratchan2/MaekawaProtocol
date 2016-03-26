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
			ServerSocket serverSock = new ServerSocket(myHost.getMe().getPort());
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
