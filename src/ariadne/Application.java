package ariadne;

import ariadne.ui.UI;
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
				
			}});
		
	}
}