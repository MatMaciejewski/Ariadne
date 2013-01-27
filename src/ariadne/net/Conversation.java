package ariadne.net;

import java.nio.channels.SocketChannel;

public class Conversation {
	public interface State{
		
	}
	private SocketChannel socket;
	private State state;
	
	public Conversation(SocketChannel socket, State state){
		this.socket = socket;
		this.state = state;
	}
	
	public SocketChannel getSocket(){
		return socket;
	}
	
	public State getState(){
		return state;
	}
	
	public void setState(State state){
		this.state = state;
	}
}
