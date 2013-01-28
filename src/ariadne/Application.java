package ariadne;

import java.util.Set;

import ariadne.data.Hash;
import ariadne.net.Address;
import ariadne.net.Client;
import ariadne.net.Server;
import ariadne.ui.UI;
import ariadne.ui.DelegableUI.HashAddedEvent;
import ariadne.ui.UI.Event;
import ariadne.ui.UI.Listener;
import ariadne.ui.graphic.GraphicUI;

public class Application{
	private static int innerPort;
	private static int outerPort;
	private static Server server;
	private static Client client;
	private static UI ui;
	private static TaskManager manager;
	
	private static void initialise(){
		ui = new GraphicUI();
		manager = new Manager();
		server = new Server(innerPort);
		client = new Client(new Address("192.168.1.113", outerPort));
		prepareUI();
		server.start(1);
	}
	
	private static void finalise(){
		server.stop();
		for(Hash h: manager.getTasks()){
			manager.removeTask(h);
		}
	}

	public static void run(int in, int out){
		innerPort = in;
		outerPort = out;
		
		initialise();
		ui.open();
		ui.eventLoop();
		ui.close();
		finalise();
	}
	
	public static Client getClient(){
		return client;
	}
	
	public static TaskManager getManager(){
		return manager;
	}

	private static void prepareUI(){
		
		
		ui.onClosing(new Listener(){
			@Override
			public void trigger(Event e) {
				ui.breakEventLoop();
			}});
		
		ui.onHashAdded(new Listener(){
			@Override
			public void trigger(Event e) {
				HashAddedEvent h = (HashAddedEvent) e;
				
				try{
					//7815696ecbf1c96e6894b779456d330e
					Hash hash = new Hash(h.data);
					System.out.println(hash);
					
					manager.insertTask(hash, "./", "cat.jpg");
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("Invalid hash");
				}
				
			}});
	}
}