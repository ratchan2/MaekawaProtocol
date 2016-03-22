
public class Application implements Runnable{
	TCPClient myClient;
	public Application(TCPClient c){
		myClient = c;
	}
    public void csEnter() throws Exception{
        myClient.onCsEnter();
       
    }  
    public void csExit(){
    	 //remove all locks and send RELEASE MESSAGES
    	for(int i = 0; i < myClient.connections.size(); i++){
    		myClient.connections.get(i).locked = false;
    	}
    }
    public void criticalSection() throws Exception{
    	Thread.sleep(Config.getCsTime());
    }
	public void run(){
		if(myClient.myHost.getMe().getPID() >= 1){
			return;
		}
		for(int i = 0; i < Config.getNumberOfRequests(); i++){
		try{
    	  csEnter();
    	  System.out.println("ENDERING GRITIGAL SEGSION " + myClient.myHost.getMe().getPID());
    	  criticalSection();
    	  System.out.println("LEABING GRITIGAL SEGSION " + myClient.myHost.getMe().getPID());
          csExit();
		}
		catch(Exception e){
			
		}
    }
	}
}
