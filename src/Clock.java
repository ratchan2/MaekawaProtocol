
public class Clock {
      public static int value = 0;
       public synchronized static void incrClock(){
    	  value++;
      }
      public static int getValue(){
    	  return value;
      }
	 public synchronized static void updateClock(int value) {
		  if(Clock.value < value){
			  Clock.value = value + 1;
		  }
		  else{
			  Clock.value = Clock.value + 1;
		  }
	}
      
      
}
