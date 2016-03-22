
public class Application implements Runnable{
	TCPClient myClient;
	public Application(TCPClient c){
		myClient = c;
	}
    public void csEnter() throws Exception{
        myClient.onCsEnter();
       
    }  
    public void csExit(){
    	
    }
    public void criticalSection() throws Exception{
    	Thread.sleep(Config.getCsTime());
    }
	public void run(){
		try{
    	  csEnter();
    	  criticalSection();
          csExit();
		}
		catch(Exception e){
			
		}
    }
}
