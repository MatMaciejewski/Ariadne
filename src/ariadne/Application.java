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
				System.out.println(h.data);
				
				ui.showEntry(Hash.computeFromString(h.data), h.data, 50, 129, 23);
			}});
		
		
		ui.open();
		ui.eventLoop();
		ui.close();
		
	}
}