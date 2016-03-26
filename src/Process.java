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
	public static Integer lockingCount = 0;
	 public static int quorumSize = 0;
	 public static boolean receivedFail = false;
}
public class Process{
	 
	 public static ServerState ss = new ServerState();
	 public static ClientState cs = new ClientState();
	 public static int sendingClock = 0;
	 
	 public static void main(String args[]) throws Exception{
		 Config.setMe(Integer.parseInt(args[1]));
		 Host h = Config.readFile(args[0]);
		 Process.cs.quorumSize = h.quorumList.size();
		 TCPServer server = new TCPServer();
		 TCPClient client = new TCPClient(h,server);
		 Application app = new Application(client);
		 client.setData(server,h);
		 server.setData(client,h);
		 app.setData(h);
		 Logger.logsOff = true;
	     Thread serverThread = new Thread(server);
	     Thread clientThread = new Thread(client);
	     Thread applicationThread = new Thread(app);
	     serverThread.start();
	     clientThread.start();
	     applicationThread.start();
	 }
}


