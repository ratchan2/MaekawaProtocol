import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.net.*;

public class Process{
	
	 public static void main(String args[]) throws Exception{
		 Config.setMe(Integer.parseInt(args[1]));
		 Host h = Config.readFile(args[0]);
		 TCPClient client = new TCPClient();
		 TCPServer server = new TCPServer();
		 Application app = new Application(client);
		 client.setData(server,h);
		 server.setData(client,h);
		 
	     Thread serverThread = new Thread(server);
	     Thread applicationThread = new Thread(app);
	     serverThread.start();
	     applicationThread.start();
	 }
}


