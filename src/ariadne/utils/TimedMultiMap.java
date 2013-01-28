package ariadne.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is a multimap where elements are removed after specified timeout. Users can also register listeners on certain keys - this way they get notified about new elements.
 * @author eipifi
 *
 * @param <K> key type
 * @param <V> value type
 */
public class TimedMultiMap<K, V> {
	
	/**
	 * Listener class (sort of). An instance of this class can be registered as listener and asked about new values.
	 * @author eipifi
	 *
	 */
	public class KeyAddedListener{
		private Queue<V> values;
		public KeyAddedListener(){
			values = new ConcurrentLinkedQueue<V>();
		}
		public V getNext(){
			return values.poll();
		}
		private void add(V val){
			values.add(val);
		}
	}
	
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
	
	private class ValueContainer{
		public Queue<ValueEntry> entries;
		private Set<KeyAddedListener> listeners;
		public ValueContainer(){
			entries = new PriorityQueue<ValueEntry>();
			listeners = new HashSet<KeyAddedListener>(); //should be concurrent
		}
		
		public ValueEntry peek(){
			return entries.peek();
		}
		
		public ValueEntry poll(){
			return entries.poll();
		}
		
		public void add(ValueEntry v){
			entries.add(v);
			for(KeyAddedListener l: listeners){
				l.add(v.value);
			}
		}
		
		public void addListener(KeyAddedListener l){
			listeners.add(l);
		}
		public void removeListener(KeyAddedListener l){
			listeners.remove(l);
		}
		public int size(){
			return entries.size();
		}
	}

	private Map<K, ValueContainer> map;
	private Queue<KeyEntry> timeouts;
	private int elementCount;

	/**
	 * Constructor. Initialises a map.
	 */
	public TimedMultiMap() {
		map = new ConcurrentHashMap<K, ValueContainer>();
		timeouts = new PriorityQueue<KeyEntry>();
		elementCount = 0;
	}

	/**
	 * Inserts a new key-value pair with a specified timeout.
	 * @param key
	 * @param value
	 * @param timeout
	 */
	public void add(K key, V value, long timeout) {
		ValueContainer q = map.get(key);
		if (q == null) {
			q = new ValueContainer();
			map.put(key, q);
		}
		q.add(new ValueEntry(value, timeout));
		timeouts.add(new KeyEntry(key, timeout));
		elementCount++;
	}

	/**
	 * Returns a specified number of values connected with a given key
	 * @param key
	 * @param count max number of returned elements
	 * @return
	 */
	public Set<V> get(K key, int count) {
		ValueContainer q = map.get(key);
		if (q == null)
			return Collections.emptySet();
		HashSet<V> result = new HashSet<V>();
		int i = 0;
		for (ValueEntry v : q.entries) {
			if (i++ >= count)
				break;
			result.add(v.value);
		}

		return result;
	}
	
	/**
	 * Returns random values, connected with any key.
	 * @param count
	 * @return
	 */
	public Set<V> getRandom(int count){
		throw new IllegalArgumentException("Not yet implemented");
	}

	/**
	 * Returns the global element count
	 * @return
	 */
	public int size() {
		return elementCount;
	}

	/**
	 * Garbage collector function - removes all timeouted elements.
	 * @param timeout a threshold value - all elements with timeouts less than or equal to this value are removed.
	 */
	public void removeTimeouted(long timeout) {
		KeyEntry k;
		ValueEntry v;
		ValueContainer q;
		while (true) {
			k = timeouts.peek();
			if(k == null) return;
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
	
	public KeyAddedListener getListener(K key){
		ValueContainer q = map.get(key);
		if (q == null) {
			q = new ValueContainer();
			map.put(key, q);
		}
		KeyAddedListener l = new KeyAddedListener();
		q.addListener(l);
		return l;
	}
	
	public void removeListener(K key, KeyAddedListener l){
		ValueContainer q = map.get(key);
		if (q != null) {
			q.removeListener(l);
		}
	}
	
	

}
