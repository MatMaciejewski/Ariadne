package ariadne.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TimedMultiMap<K, V> {
	private static class Entry implements Comparable<Entry> {
		public long timeout;

		@Override
		public int compareTo(Entry o) {
			return (int) (timeout - o.timeout);
		}

	}

	private class KeyEntry extends Entry {
		public K key;

		public KeyEntry(K key, long timeout) {
			this.timeout = timeout;
			this.key = key;
		}
	}

	private class ValueEntry extends Entry {
		public V value;

		public ValueEntry(V value, long timeout) {
			this.timeout = timeout;
			this.value = value;
		}
	}

	private Map<K, Queue<ValueEntry>> map;
	private Queue<KeyEntry> timeouts;
	private int elementCount;

	public TimedMultiMap() {
		map = new ConcurrentHashMap<K, Queue<ValueEntry>>();
		timeouts = new PriorityQueue<KeyEntry>();
		elementCount = 0;
	}

	public void add(K key, V value, long timeout) {
		Queue<ValueEntry> q = map.get(key);
		if (q == null) {
			q = new PriorityQueue<ValueEntry>();
			map.put(key, q);
		}

		q.add(new ValueEntry(value, timeout));
		timeouts.add(new KeyEntry(key, timeout));
		elementCount++;
	}

	public Set<V> get(K key, int count) {
		Queue<ValueEntry> q = map.get(key);
		if (q == null)
			return Collections.emptySet();
		HashSet<V> result = new HashSet<V>();
		int i = 0;
		for (ValueEntry v : q) {
			if (i++ >= count)
				break;
			result.add(v.value);
		}

		return result;
	}
	
	public Set<V> getRandom(int count){
		throw new IllegalArgumentException("Not yet implemented");
	}

	public int size() {
		return elementCount;
	}

	public void removeTimeouted(long timeout) {
		KeyEntry k;
		ValueEntry v;
		Queue<ValueEntry> q;
		while (true) {
			k = timeouts.peek();
			if (k.timeout > timeout)
				return;
			k = timeouts.poll();
			q = map.get(k.key);
			if (q != null) {
				while (q.size() > 0) {
					v = q.peek();
					if (v.timeout <= timeout) {
						q.poll();
						elementCount--;
					} else
						break;
				}
			}
		}
	}

}
