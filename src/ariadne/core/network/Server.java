package ariadne.core.network;

public class Server {
	private SocketManager socketManager;
	
	public Server(int port){
		socketManager = new SocketManager(port);
	}
	
	public void start(int threads){
		try {
			socketManager.start();
		} catch (Exception e) {
			System.out.println("Error:");
			e.printStackTrace();
		}
		AbstractServerWorker s;
		for(int i=0;i<threads;++i){
			s = new ServerWorker();
			s.setSocketManager(socketManager);
			s.start();
		}
	}
	
	public void stop(){
		try {
			socketManager.stop();
		} catch (Exception e) {
			System.out.println("Error:");
			e.printStackTrace();
		}
	}
	
	public int getPort(){
		return socketManager.getPort();
	}
}
