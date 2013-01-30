package ariadne.ui;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ariadne.data.Hash;

public abstract class DelegableUI implements UI {
	public static class HashAddedEvent implements Event{
		public String data;
	}
	public static class HashRemovedEvent implements Event{
		public Hash hash;
		public boolean removeFromDisk;
	}
	public static class FileAddedEvent implements Event{
		public String path;
		public String name;
	}
	public static class FilePropertiesEvent implements Event{
		public Hash hash;
	}
	
	private class Pair{
		public Listener l;
		public Event e;
		public Pair(Listener l, Event e){
			this.l = l;
			this.e = e;
		}
	}
	public class Delegate{
		private Delegate(){}
		
		public void hashAdded(String data){
			HashAddedEvent e = new HashAddedEvent();
			e.data = data;
			eventQueue.add(new Pair(hashAdded, e));
		}
		
		public void hashRemoved(Hash hash, boolean removeFromDisk){
			HashRemovedEvent e = new HashRemovedEvent();
			e.hash = hash;
			e.removeFromDisk = removeFromDisk;
			eventQueue.add(new Pair(hashRemoved, e));
		}
		
		public void fileAdded(String name, String path){
			FileAddedEvent e = new FileAddedEvent();
			e.path = path;
			e.name = name;
			eventQueue.add(new Pair(fileAdded, e));
		}
		
		public void fileProperties(Hash hash){
			FilePropertiesEvent e = new FilePropertiesEvent();
			e.hash = hash;
			eventQueue.add(new Pair(fileProperties, e));
		}
		
		public void closing(){
			eventQueue.add(new Pair(closing, null));
		}
	}

	private Delegate delegate;
	private Queue<Pair> eventQueue;
	private boolean eventLoop;
	
	private Listener hashAdded;
	private Listener closing;
	private Listener hashRemoved;
	private Listener fileAdded;
	private Listener fileProperties;
	
	
	
	public DelegableUI(){
		eventQueue = new ConcurrentLinkedQueue<Pair>();
	}
	
	protected Delegate getDelegateInstance(){
		if(delegate == null) delegate = new Delegate();
		return delegate;
	}
	
	@Override
	public void onClosing(Listener l) {
		closing = l;

	}

	@Override
	public void onHashAdded(Listener l) {
		hashAdded = l;

	}

	@Override
	public void onHashRemoved(Listener l) {
		hashRemoved = l;
	}
	
	@Override
	public void onFileProperties(Listener l){
		fileProperties = l;
	}
	
	@Override
	public void breakEventLoop(){
		eventLoop = false;
	}
	
	@Override
	public void onFileAdded(Listener l){
		fileAdded = l;
	}
	
	@Override
	public void eventLoop(){
		eventLoop = true;
		Pair p;
		while(eventLoop){
			p = eventQueue.poll();
			if(p == null){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}else{
				if(p.l != null){
					p.l.trigger(p.e);
				}
			}
		}
	}
}
