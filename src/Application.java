
public class Application implements Runnable{
	TCPClient myClient;
	public Host myHost;
	public Application(TCPClient c){
		myClient = c;
	}
	
	public void csEnter() throws Exception{
		myClient.onCsEnter();

	}  
	public void setData(Host h){
		myHost = h;
	}
	public void csExit(){
		//remove all locks and send RELEASE MESSAGES
		synchronized(Process.cs){
			for(int i = 0; i < myClient.connections.size(); i++){
				Process.cs.locks.put(myClient.connections.get(i).quorumMember.getPID(),false);
				Process.cs.fails.put(myClient.connections.get(i).quorumMember.getPID(),false);

			}
			Process.cs.inCS = false;
		}
		for(int i = 0; i < myClient.connections.size(); i++){
			myClient.connections.get(i).sendRelease();
		}
	}
	public void criticalSection() throws Exception{

		Thread.sleep(Config.getCsTime());
	}
	public void run(){
		for(int i = 0; i < Config.getNumberOfRequests(); i++){

			try{

				csEnter();
				Logger.log(myHost,"ENDERING GRITIGAL SEGSION " + myClient.myHost.getMe().getPID());
				criticalSection();
				Logger.log(myHost,"LEABING GRITIGAL SEGSION " + myClient.myHost.getMe().getPID());
				csExit();
				Thread.sleep(Config.getRequestDelay());

			}
			catch(Exception e){

			}
		}
	}

}
