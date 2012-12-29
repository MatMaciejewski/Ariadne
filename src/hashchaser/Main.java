package hashchaser;

import hashchaser.core.Address;
import hashchaser.core.Application;
import hashchaser.core.Hash;
import hashchaser.core.Hash.InvalidHashException;
import hashchaser.core.protocol.ChaseResponse;
import hashchaser.gui.GUI;
import hashchaser.gui.WindowedGUI;
import hashchaser.gui.GUI.Event;
import hashchaser.gui.GUI.HashAddedEvent;

public class Main {
	
	private static Application app;

	public static void main(String[] args) {
		
		app = new Application();
		app.start();
		
		GUI gui = new WindowedGUI();
		
		gui.onHashAdd(new GUI.Listener() {
			public void act(Event e) {
				GUI.HashAddedEvent ev = (HashAddedEvent) e;
				System.out.println("Chasing hash " + ev.hash);
				try {
					ChaseResponse response;
					response = app.sendChase(Address.fromString("127.0.0.1"), new Hash(ev.hash), 1000);
					System.out.println("Response: " + response);
				} catch (InvalidHashException e1) {
					System.out.println("Invalid hash provided.");
				}
								
			}
		});
		
		gui.start();
		gui.eventLoop();
		gui.stop();
		
		app.stop();
		echo("stopped!");
	}
	
	public static void echo(String s){
		System.out.println(s);
	}

}
