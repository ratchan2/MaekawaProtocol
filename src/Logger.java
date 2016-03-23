
public class Logger {
	 public static boolean logsOff = false;
     public static void log(Host h, String line){
    	 if(!logsOff){
    		 System.out.println(h.getMe().getPID() + ">" + line);
    	 }
     }
}
