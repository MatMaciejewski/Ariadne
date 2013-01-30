package ariadne.data;

import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ariadne.net.Address;
import ariadne.utils.OldTimedMultiMap;

public class OldCatalogue {
	public static class Listener{
		private OldTimedMultiMap<Hash, Address>.KeyAddedListener listener;
		private Hash keyHash;
		private Listener(Hash h){
			keyHash = h;
			listener = peers.getListener(h);
		}
		public Address getNext(){
			return listener.getNext();
		}
		public void disable(){
			peers.removeListener(keyHash, listener);
		}
		public Hash getHash(){
			return keyHash;
		}
	}
	private static class Task {
		public Hash hash;
		public Address peer;
		public long timeout;
		public boolean ignoreEvents;

		public Task(Hash hash, Address peer, long timeout, boolean ignoreEvents) {
			this.hash = hash;
			this.peer = peer;
			this.timeout = timeout;
			this.ignoreEvents = ignoreEvents;
		}
	}

	public static final int DEF_TIMEOUT = 600000;
	private static OldTimedMultiMap<Hash, Address> peers;
	private static Queue<Task> tasks;
	private static ReentrantReadWriteLock rwl;
	private static Lock r;
	private static Lock w;

	public static void initialize() {
		peers = new OldTimedMultiMap<Hash, Address>();
		tasks = new ConcurrentLinkedQueue<Task>();
		rwl = new ReentrantReadWriteLock();
		r = rwl.readLock();
		w = rwl.writeLock();
	}

	public static int getPeerNumber(){
		return peers.size();
	}
	
	public static List<Address> getPeerForHash(Hash hash, int val) {
		List<Address> result;
		r.lock();
		result = peers.get(hash, val);
		r.unlock();
		return result;
	}

	public static List<Address> getRandomPeers(int count) {
		List<Address> result;
		r.lock();
		result = peers.getRandom(count);
		r.unlock();
		return result;
	}
	
	public static Listener getListener(Hash h){
		return new Listener(h);
	}

	public static void addPeer(Hash hash, Address peer, long timeout, boolean ignoreEvents) {
		tasks.add(new Task(hash, peer, timeout, ignoreEvents));
	}

	public static void update() {
		w.lock();
		long time = new Date().getTime();
		peers.removeTimeouted(time);

		while(tasks.size() > 0) {
			Task t = tasks.poll();
			peers.add(t.hash, t.peer, time + t.timeout, t.ignoreEvents);
		}
		w.unlock();
	}
}
