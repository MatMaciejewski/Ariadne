package hashchaser.gui;

public abstract class GUI {
	public static abstract class Event {
	};
	public static interface Listener {
		public void act(Event e);
	};
	private class EventPair {

		public Listener l;
		public Event e;

		public EventPair(Listener l, Event e) {
			this.l = l;
			this.e = e;
		}
	}
	public class QueueDelegate {
		private GUI gui;

		private QueueDelegate(GUI g) {
			gui = g;
		}

		private void addWork(Listener l, Event e) {
			if (l != null) {
				work.add(new EventPair(l, e));
				if (waiting) {
					synchronized (gui) {
						gui.notifyAll();
					}
				}
			}
		}

		public void hashAdded(String s) {
			HashAddedEvent e = new HashAddedEvent();
			e.hash = s;
			addWork(listenerOnHashAdd, e);
		}

		public void quit() {
			loop = false;
			if (waiting) {
				synchronized (gui) {
					gui.notifyAll();
				}
			}
		}
	}

	public abstract void start();
	public abstract void stop();
	public abstract void setFileData(String hash, String name, float progress, float download, float upload);
	public abstract void removeFileData(String hash);
	
	///////////////////////////////////////////

	private java.util.concurrent.ConcurrentLinkedQueue<EventPair> work;
	private boolean loop;
	private boolean waiting = false;

	public GUI() {
		work = new java.util.concurrent.ConcurrentLinkedQueue<EventPair>();
	}

	protected QueueDelegate getDelegate() {
		return new QueueDelegate(this);
	}

	public synchronized void eventLoop() {
		loop = true;
		EventPair p;
		while (loop) {
			p = work.poll();
			if (p != null) {
				try{
					p.l.act(p.e);
				}catch(Exception e){
					System.out.println("CRITICAL EXCEPTION INTERCEPTED:");
					e.printStackTrace();
					System.out.println("PLEASE REPORT THIS BUG!");
				}
			} else {
				waiting = true;
				try {
					this.wait(1000);
				} catch (InterruptedException e) {
				}
				waiting = false;
			}
		}
	}

	///////////////////////////
	private Listener listenerOnHashAdd;
	
	public static class HashAddedEvent extends Event {
		public String hash;
	}

	public void onHashAdd(Listener listener) {
		listenerOnHashAdd = listener;
	}

}
