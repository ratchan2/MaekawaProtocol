import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.net.*;
import java.util.*;
class ServerState{
	 public PriorityQueue<Message> waitingQueue = new PriorityQueue<Message>(1000,new MessageComparator());
	 public Message lockingRequest = null;
};
class ClientState{
	public HashMap<Integer,Boolean> locks = new HashMap<Integer,Boolean>();
	public HashMap<Integer,Boolean> fails = new HashMap<Integer,Boolean>();
	public boolean inCS = false;
}
public class Process{
	 
	 public static ServerState ss = new ServerState();
	 public static ClientState cs = new ClientState();
	 public static Clock clock = new Clock();
	 public static void main(String args[]) throws Exception{
		 Config.setMe(Integer.parseInt(args[1]));
		 Host h = Config.readFile(args[0]);
		 TCPClient client = new TCPClient();
		 TCPServer server = new TCPServer();
		 Application app = new Application(client);
		 client.setData(server,h);
		 server.setData(client,h);
		 app.setData(h);
		 Logger.logsOff = false;
	     Thread serverThread = new Thread(server);
	     Thread clientThread = new Thread(client);
	     Thread applicationThread = new Thread(app);
	     serverThread.start();
	     clientThread.start();
	     applicationThread.start();
	 }
}


