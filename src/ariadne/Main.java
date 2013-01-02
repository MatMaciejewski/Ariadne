package ariadne;

import ariadne.core.Address;
import ariadne.core.Application;
import ariadne.core.Hash;
import ariadne.core.Hash.InvalidHashException;
import ariadne.core.protocol.ChaseResponse;
import ariadne.gui.GUI;
import ariadne.gui.WindowedGUI;
import ariadne.gui.GUI.Event;
import ariadne.gui.GUI.HashAddedEvent;

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
