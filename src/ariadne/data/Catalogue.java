package ariadne.data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ariadne.net.Address;
import ariadne.utils.ActuallyNotTimedMultiMap;
import ariadne.utils.TimedMultiMap;

public class Catalogue {
	public static class Listener{
		private Queue<Address> q;
		private Hash keyHash;
		private Listener(Hash h){
			keyHash = h;
			q = new ConcurrentLinkedQueue<Address>();
		}
		public Address getNext(){
			return q.poll();
		}
		public void disable(){
			listeners.remove(getHash());
		}
		public Hash getHash(){
			return keyHash;
		}
		private void add(Address a){
			q.add(a);
		}
	}
	public static final int DEF_TIMEOUT = 600000;
	private static TimedMultiMap<Hash, Address> peers;
	private static Map<Hash, Listener> listeners;
	private static ReentrantReadWriteLock rwl;
	private static Lock r;
	private static Lock w;

	public static void initialize() {
		peers = new ActuallyNotTimedMultiMap<Hash, Address>();
		listeners = new HashMap<Hash, Listener>();
		rwl = new ReentrantReadWriteLock();
		r = rwl.readLock();
		w = rwl.writeLock();
	}

	public static int getPeerNumber(){
		int n = peers.size();
		return n;
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
	
	public static Catalogue.Listener getListener(Hash h){
		r.lock();
		Listener listener = new Listener(h);
		listeners.put(h, listener);
		r.unlock();
		return listener;
	}

	public static void addPeer(Hash hash, Address peer, long timeout, boolean ignoreEvents) {
		w.lock();
		Listener l = listeners.get(hash);
		peers.add(hash, peer, timeout);
		w.unlock();
		if(l != null){
			l.add(peer);
		}
	}
	public static void update() {
		w.lock();
		long time = new Date().getTime();
		peers.removeTimeouted(time);
		w.unlock();
	}
}