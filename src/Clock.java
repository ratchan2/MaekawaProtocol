
public class Clock {
      public Integer value = 0;
       public void incrClock(){
    	   synchronized(this.value){
    	      value++;
    	   }
      }
      public int getValue(){
    	  return value;
      }
	 public void updateClock(int value) {
		 synchronized(this.value){
		  if(this.value < value){
			  this.value = value + 1;
		  }
		  else{
			  this.value = this.value + 1;
		  }
		 }
	}
      
      
}
