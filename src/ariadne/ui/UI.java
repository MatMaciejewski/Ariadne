package ariadne.ui;

import ariadne.data.Hash;

public interface UI {
	public interface Event{}
	public interface Listener{
		public void trigger(Event e);
	}
	
	public void open();
	public void close();
	
	public void showEntry(Hash hash, String name, float size, float percent, float downRate, float upRate, float ratio);
	public void dropEntry(Hash hash);
	
	public void onHashAdded(Listener l);
	public void onHashRemoved(Listener l);
	public void onClosing(Listener l);
	
	public void breakEventLoop();
	public void eventLoop();
}
