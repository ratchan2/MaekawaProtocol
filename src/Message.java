public class Message{
	public int requestID;
	public int pid;
	public String messageType;
	public int getRequestID(){
		return requestID;
	}
	public int getPID(){
		return pid;
	}
	public void setRequestId(int id){
		requestID = id;
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
	public Message(int rid,int pid,String type){
		this.requestID = rid;
		this.pid = pid;
		this.messageType = type;
	}
}