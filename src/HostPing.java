import java.io.BufferedReader;
import java.io.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;


public class HostPing implements Runnable{
	TCPClient myClient;
	TCPServer myServer;

	public Node quorumMember;
	ClientRequest myRequest;
	// BufferedReader reader;
	Host myHost;
	public  boolean hostPing = false;
	boolean receivedFail = false;

	public HostPing(TCPClient c, TCPServer server, Node q,Host h,ClientRequest request){
		myClient = c;
		myServer = server;
		quorumMember  = q;
		myHost = h;
		myRequest = request;

	}
	public void run(){
		
	}
//	public void run() {
//		while(!hostPing){
//			try{
//
//				myRequest.socket = new Socket(quorumMember.getHostName()+ ".utdallas.edu",quorumMember.getPort());
//				myRequest.writer = new PrintWriter(myRequest.socket.getOutputStream());	
//				myRequest.reader = new BufferedReader(new InputStreamReader(myRequest.socket.getInputStream()));
//				hostPing = true;
//				
//				synchronized(myRequest){
//					myRequest.signal = true;
//					myRequest.notifyAll();
//				}
//				return;
//			}
//			catch(Exception e){
//
//			}
//		}
//
//	}
}
