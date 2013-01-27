package ariadne;

import ariadne.data.Hash;
import ariadne.data.Settings;
import ariadne.ui.UI;
import ariadne.ui.DelegableUI.HashAddedEvent;
import ariadne.ui.UI.Event;
import ariadne.ui.UI.Listener;
import ariadne.ui.graphic.GraphicUI;


public class Application{
	
	public static void main(String[] args){
		
		Settings sets = new Settings();
		sets.updateSettings(new Hash("ABABABABAABBABABABABABABABABABA5"), "Nazwa5", "Sciezka5");
		sets.updateSettings(new Hash("ABABABABAABBABABABABABABABABABA6"), "Nazwa6", "Sciezka6");
		Hash[] temp = sets.getAllHash();
		for (Hash tmp : temp){
			System.out.println(tmp);
		}
		sets.updateSettingsFile();
		/*final UI ui = new GraphicUI();
		
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
		
		ui.showEntry(Hash.computeFromString("1"), "file", 192.67f, 0.23f, 652.44f, 56.45f, 0.12f);
		ui.showEntry(Hash.computeFromString("2"), "file", 192.67f, 0.23f, 652.44f, 56.45f, 0.12f);
		ui.showEntry(Hash.computeFromString("3"), "file", 192.67f, 0.23f, 652.44f, 56.45f, 0.12f);
		ui.showEntry(Hash.computeFromString("4"), "file", 192.67f, 0.23f, 652.44f, 56.45f, 0.12f);
		ui.showEntry(Hash.computeFromString("5"), "file", 192.67f, 0.23f, 652.44f, 56.45f, 0.12f);
		ui.showEntry(Hash.computeFromString("6"), "file", 192.67f, 0.23f, 652.44f, 56.45f, 0.12f);
		
		ui.eventLoop();
		ui.close();*/
		

	}
}