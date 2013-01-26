package ariadne;

import ariadne.data.Hash;
import ariadne.ui.UI;
import ariadne.ui.DelegableUI.HashAddedEvent;
import ariadne.ui.UI.Event;
import ariadne.ui.UI.Listener;
import ariadne.ui.graphic.GraphicUI;


public class Application{
	
	public static void main(String[] args){
		final UI ui = new GraphicUI();
		
		ui.onClosing(new Listener(){
			@Override
			public void trigger(Event e) {
				ui.breakEventLoop();
			}});
		
		ui.onHashAdded(new Listener(){
			@Override
			public void trigger(Event e) {
				HashAddedEvent h = (HashAddedEvent) e;
				System.out.println("Text entered: " + h.data);
				ui.showEntry(Hash.computeFromString(h.data), h.data, 192.67f, 0.23f, 652.44f, 56.45f, 0.12f);
			}});
		
		ui.open();
		ui.eventLoop();
		ui.close();
		

	}
}