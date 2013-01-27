package ariadne.data;

import java.util.Date;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ariadne.net.Address;
import ariadne.utils.TimedMultiMap;

public class Catalogue {
	private static class Task {
		public Hash hash;
		public Address peer;
		public long timeout;

		public Task(Hash hash, Address peer, long timeout) {
			this.hash = hash;
			this.peer = peer;
			this.timeout = timeout;
		}
	}

	private static TimedMultiMap<Hash, Address> peers;
	private static Queue<Task> tasks;
	private static final int PEERS_NUMBER = 10;
	private static final int RANDOM_PEERS = 10;
	private static ReentrantReadWriteLock rwl;
	private static Lock r;
	private static Lock w;

	public static void initialize() {
		peers = new TimedMultiMap<Hash, Address>();
		tasks = new ConcurrentLinkedQueue<Task>();
		rwl = new ReentrantReadWriteLock();
		r = rwl.readLock();
		w = rwl.writeLock();
	}

	public static Set<Address> getPeerForHash(Hash hash) {
		Set<Address> result;
		r.lock();
		result = peers.get(hash, PEERS_NUMBER);
		r.unlock();
		return result;
	}

	public static Set<Address> getRandomPeers() {
		Set<Address> result;
		r.lock();
		result = peers.getRandom(RANDOM_PEERS);
		r.unlock();
		return result;
	}

	public static void addPeer(Hash hash, Address peer, long timeout) {
		tasks.add(new Task(hash, peer, timeout * 1000));
	}

	public static void update() {
		w.lock();
		long time = new Date().getTime();
		peers.removeTimeouted(time);

		for (int i = tasks.size(); i >= 0; --i) {
			Task t = tasks.poll();
			peers.add(t.hash, t.peer, time + t.timeout);
		}
		w.unlock();
	}
}
