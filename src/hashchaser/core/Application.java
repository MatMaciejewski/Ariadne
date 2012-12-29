package hashchaser.core;

import hashchaser.core.network.Finder;
import hashchaser.core.network.Service;
import hashchaser.core.protocol.ChaseMessage;
import hashchaser.core.protocol.ChaseResponse;

public class Application {
	public static final int DEFAULT_PORT = 25566;
	
	private Service service;

	public Application(){
		service = new Service(DEFAULT_PORT, DEFAULT_PORT);
	}
	
	public void start(){
		service.start();
	}
	
	public void stop(){
		service.stop();
	}
	
	public ChaseResponse sendChase(Address address, Hash hash, int timeout){
		ChaseResponse response = new ChaseResponse();
		boolean result = Finder.ask(new ChaseMessage(hash), address, response, timeout);
		return result ? response : null;
	}
}
