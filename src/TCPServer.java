import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
public class TCPServer implements Runnable{
	public int pid;
	public TCPClient myClient;
	public Host myHost;
	LinkedBlockingQueue<Message> waitingQueue = new LinkedBlockingQueue<Message>();
	public int lockedTo;
	
	public void setPID(int id){
		pid = id;
	}
	
	public void run(){
		try
		{
			ServerSocket serverSock = new ServerSocket(myHost.getMe().getPort());
			while(true){
			
				Socket sock = serverSock.accept();
			 	ServerSock currentSock = new ServerSock(sock,this,myHost);
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
