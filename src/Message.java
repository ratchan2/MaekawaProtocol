import java.util.Comparator;

public class Message{
	public int pid;
	public int clock;
	public String messageType;
	public int getPID(){
		return pid;
	}
	@Override
	String toString(){
		return (messageType + "~" + pid + "~" + clock);
	}
	public int getClock(){
		return clock;
	}
	public void setPID(int pid){
		this.pid = pid;
	}
	public String getMessageType(){
		return messageType;
	}
	public void setMessageType(String type){
		messageType = type;
	}
	public Message(int clock,int pid,String type){
		this.clock = clock;
		this.pid = pid;
		this.messageType = type;
	}
}

class MessageComparator implements Comparator<Message>
{
    @Override
    public int compare(Message x, Message y)
    {
        
        if (x.getClock() < y.getClock() || (x.getClock() == y.getClock() && x.getPID() < y.getPID()))
        {
            return -1;
        }
        return 1;
        
    }
}

