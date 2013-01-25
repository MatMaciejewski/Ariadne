package ariadne;

import ariadne.net.Server;
import ariadne.ui.UI;
import ariadne.ui.DelegableUI.HashAddedEvent;
import ariadne.ui.UI.Event;
import ariadne.ui.UI.Listener;
import ariadne.ui.graphic.GraphicUI;

public class Ariadne {
	public static void main(String[] args){
		
		final Server s = new Server(25566);
		s.start(4);
		
		final UI ui = new GraphicUI();
		ui.open();
		ui.onClosing(new Listener(){
			public void trigger(Event e) {
				ui.close();
				s.stop();
			}});
		
		ui.onHashAdded(new Listener(){
			public void trigger(Event e) {
				HashAddedEvent ev = (HashAddedEvent) e;
				System.out.println(ev.data);
			}});
		
		ui.eventLoop();
		ui.close();
	
	}
}
