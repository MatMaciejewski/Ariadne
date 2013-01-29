package ariadne;


import ariadne.data.Catalogue;
import ariadne.data.Database;
import ariadne.data.Descriptor;
import ariadne.data.File;
import ariadne.data.Hash;
import ariadne.net.Address;
import ariadne.net.Client;
import ariadne.net.Server;
import ariadne.ui.UI;
import ariadne.ui.DelegableUI.FileAddedEvent;
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
		client = new Client(new Address("127.0.0.1", outerPort));
		
		Database.initialize();
		Catalogue.initialize();
		Catalogue.update();
		
		prepareUI();
		server.start(1);
	}
	
	private static void finalise(){
		server.stop();
		manager.closeAllTasks();
	}

	public static void run(int in, int out){
		innerPort = in;
		outerPort = out;
		
		initialise();
		ui.open();
		try {
			ui.eventLoop();
		} catch (Exception e) {
			System.out.println("CRITICAL ERROR IN eventLoop -------------------------------");
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		
		ui.close();
		finalise();
	}
	
	public static Client getClient(){
		return client;
	}
	
	public static TaskManager getManager(){
		return manager;
	}
	
	public static UI getUI(){
		return ui;
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
					
					//		7815696ecbf1c96e6894b779456d330e#cat.jpg	
					
					String[] parts = h.data.split("#");
					for(int i=0;i<parts.length;++i){
						System.out.println(parts[i]);
					}
					if(parts.length == 2){
						Hash hash = new Hash(parts[0]);
						String name = parts[1];
						String path = System.getProperty("user.dir");
						
						manager.insertTask(hash, path, name);
					}
					
				}catch(Exception ex){
					System.out.println("Invalid hash");
				}
			}});
		
		ui.onFileAdded(new Listener(){
			@Override
			public void trigger(Event e) {
				FileAddedEvent f = (FileAddedEvent) e;
				File file = new File(f.path, f.name, 8192);
				manager.insertTask(file);
			}
			
		});
	}
}