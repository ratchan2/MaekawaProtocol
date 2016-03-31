import java.util.Arrays;

public class Clock {
      public static Integer value = 0;
      public static int vectorClock[]=new int[Config.getNumberOfNodes()];
 
      public  static void incrClock(){
    	  synchronized(vectorClock){
    	      vectorClock[Config.getMe()]++;
      }
      }
      public static int getValue(){
    	  return vectorClock[Config.getMe()];
      }
	  public  static void updateVectorClock(String vectorClockReceived[])
      {
		  synchronized(vectorClock){
    	  for(int i=0;i<Config.getNumberOfNodes();i++)
    	  {
    		  if(i==Config.getMe())
    		  vectorClock[i]=maximum(vectorClock[i],Integer.parseInt(vectorClockReceived[i]))+1;
    		  else
    		  vectorClock[i]=maximum(vectorClock[i], Integer.parseInt(vectorClockReceived[i]));
    	  }
		  }
      }
      public  static String getVectorClock()
      {
    	  synchronized(vectorClock){
    	  String vectorClockAppend=new String();
    	  for(int i=0;i<Config.getNumberOfNodes();i++)
    	  {
    		if(i!=Config.getNumberOfNodes()-1)  
    	  vectorClockAppend+=(vectorClock[i]+"~");
    		else
    	  vectorClockAppend+=(vectorClock[i]);
    	  }
    	  return vectorClockAppend;
    	  }
      }
      public static  int maximum(int a, int b)
      {
    	  if(a<b)
    		  return b;
    	  else
    		  return a;
      }
      
     
      public static String[] readVector(String tokens []){
    	   return  Arrays.copyOfRange(tokens,2,tokens.length);
      }
      public static int returnClockValue(String tokens[],int index)
      {
    	  String [] clockArray=Clock.readVector(tokens);
    	  return Integer.parseInt(clockArray[index]);
      }
}
