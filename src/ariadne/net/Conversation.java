package ariadne.net;

import java.net.Socket;

public class Conversation {
	private Socket socket;
	
	public Conversation(Socket s){
		
	}
	
	public byte[] read(){
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public void write(byte[] data){
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
