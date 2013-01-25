package ariadne.ui;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class DelegableUI implements UI {
	public class HashAddedEvent implements Event{
		public String data;
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
	public void breakEventLoop(){
		eventLoop = false;
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
