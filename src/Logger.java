import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Logger{
	public static boolean logsOff = false;
	public static void log(Host h, String line){
		if(!logsOff){
		//	System.out.println(h.getMe().getPID() + ">" + line);
			try{
				Files.write(Paths.get("logs-" +Config.getMe() + ".out"), (h.getMe().getPID() + ">" + line + "\n").getBytes(), StandardOpenOption.APPEND);
			}
			catch(Exception e){

			}
		}
//		else{
//			try{
//				Files.write(Paths.get("foo.out"), (h.getMe().getPID() + ">" + line + "\n").getBytes(), StandardOpenOption.APPEND);
//			}
//			catch(Exception e){
//
//			}
//		}
		
		
	}
	
	public static void log( String line){
		try{
			Files.write(Paths.get("foo-" + Config.getMe() +  ".out"  ), (line + "\n").getBytes(), StandardOpenOption.APPEND);
		}
		catch(Exception e){

		}
	}
	
 
}