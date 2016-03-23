import java.io.BufferedReader;
import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;



public class TCPServer implements Runnable{
	public int pid;
	public TCPClient myClient;
	public Host myHost;
	//CopyOnWriteArrayList<Message> waitingQueue = new CopyOnWriteArrayList<Message>();
	
	Map<Integer, PrintWriter> mapNodeWriter = new HashMap<Integer,PrintWriter>();
	
	PriorityBlockingQueue<Message> waitingQueue = new PriorityBlockingQueue<Message>(1000,new MessageComparator());
	public int lockedTo;
	
    public TCPServer() {
		
	}
	
	public void setPID(int id){
		pid = id;
	}
	//TODO:Check synchronized on sentLocked and lockedPID
	public static boolean sentLocked = false;
	public static Message lockingRequest = null;
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
