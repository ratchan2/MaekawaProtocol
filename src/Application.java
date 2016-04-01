import org.apache.commons.math3.distribution.ExponentialDistribution;
public class Application implements Runnable{
	TCPClient myClient;
	public Host myHost;
	public Application(TCPClient c){
		myClient = c;
	}
	 public static ExponentialDistribution csTimeDistribution = new ExponentialDistribution((double)Config.getCsTime());
	 public static ExponentialDistribution requestDelayDistribution = new ExponentialDistribution((double)Config.getRequestDelay());
		
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
		ClientRequestV2.onCsExit();
			
     
	}
	public void criticalSection() throws Exception{

		Thread.sleep((long)csTimeDistribution.sample());
	}
	public void run(){
	   for(int i = 0; i < Config.getNumberOfRequests() ; i++){

			try{

				csEnter();
				Logger.log("E~" + Clock.getVectorClock()/*+ " Clock : " + Clock.getValue() + "Time : " + System.currentTimeMillis()*/);
				criticalSection();
			    Logger.log("L~" + Clock.getVectorClock()/*+ " Clock : " + Clock.getValue() + "Time : " + System.currentTimeMillis()*/) ;
				csExit();
				Thread.sleep((long)requestDelayDistribution.sample());

			}
			catch(Exception e){
                  Logger.log(myHost, "EGGCEPTION");
                  e.printStackTrace();
			}
		}
	}

}
