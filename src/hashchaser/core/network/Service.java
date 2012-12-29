package hashchaser.core.network;

public class Service {
	private int innerPort;
	private int outerPort;
	private Server server;
	
	
	public Service(int innerPort, int outerPort) {
		this.innerPort = innerPort;
		this.outerPort = outerPort;
		
		server = new Server(innerPort);
	}
	public void start(){
		server.start(2);
	}
	
	public void stop(){
		server.stop();
	}
	
	
	public int getInnerPort(){
		return innerPort;
	}
	public int getOuterPort(){
		return outerPort;
	}
}
