
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
	
//			for(int i = 0; i < myClient.connections.size(); i++){
//				Process.cs.locks.put(myClient.connections.get(i).quorumMember.getPID(),false);
//				Process.cs.fails.put(myClient.connections.get(i).quorumMember.getPID(),false);
//           }
			Process.cs.inCS = false;
		    ClientRequest.receivedFail = false;
			ClientRequest.lockingCount = 0;
			TCPClient.lockingCount = 0;

		for(int i = 0; i < myClient.clientRequests.size(); i++){
			   int quorumPID = myClient.clientRequests.get(i).quorumMember.getPID();
			    ClientRequestV2.sendRelease(quorumPID);
		}
		Logger.log(myHost, "Sent Release message");
     
	}
	public void criticalSection() throws Exception{

		Thread.sleep(Config.getCsTime());
	}
	public void run(){
		for(int i = 0; i < Config.getNumberOfRequests() ; i++){

			try{

				csEnter();
				System.out.println("ENDERING GRITIGAL SEGSION ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ " + myClient.myHost.getMe().getPID() + " Clock : " + Clock.getValue());
				criticalSection();
				System.out.println("LEABING GRITIGAL SEGSION ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ " + myClient.myHost.getMe().getPID() + " Clock : " + Clock.getValue()) ;
				csExit();
				Thread.sleep(Config.getRequestDelay());

			}
			catch(Exception e){
                  Logger.log(myHost, "EGGCEPTION");
                  e.printStackTrace();
			}
		}
	}

}
