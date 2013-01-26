package ariadne.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class TimedMultiMap<K,V> {
	private static class Entry implements Comparable<Entry>{
		public int timeout;

		@Override
		public int compareTo(Entry o) {
			return timeout - o.timeout;
		}
		
	}
	
	private class KeyEntry extends Entry{
		public K key;
		public KeyEntry(K key, int timeout){
			this.timeout = timeout;
			this.key = key;
		}
	}
	private class ValueEntry extends Entry{
		public V value;
		public ValueEntry(V value, int timeout){
			this.timeout = timeout;
			this.value = value;
		}
	}
	
	
	
	private Map<K, Queue<ValueEntry>>  map;
	private Queue<KeyEntry> timeouts;
	private int elementCount;
	
	
	public TimedMultiMap(){
		map = new HashMap<K, Queue<ValueEntry>>();
		timeouts = new PriorityQueue<KeyEntry>();
		elementCount = 0;
	}
	
	
	public void add(K key, V value, int timeout){
		Queue<ValueEntry> q = map.get(key);
		if(q == null){
			q = new PriorityQueue<ValueEntry>();
			map.put(key, q);
		}
		
		q.add(new ValueEntry(value, timeout));
		timeouts.add(new KeyEntry(key, timeout));
		elementCount++;
	}
	
	public Set<V> get(K key, int count){
		Queue<ValueEntry> q = map.get(key);
		if(q == null) return Collections.emptySet();
		
		Set<V> result = new HashSet<V>();
		
		int i = 0;
		for(ValueEntry v: q){
			if(i++ >= count) break;
			result.add(v.value);
		}
		
		return result;
	}
	
	public int size(){
		return elementCount;
	}
	
	public void removeTimeouted(int timeout){
		KeyEntry k;
		ValueEntry v;
		Queue<ValueEntry> q;
		while(true){
			k = timeouts.peek();
			if(k.timeout > timeout) return;
			k = timeouts.poll();
			q = map.get(k.key);
			if(q != null){
				while(q.size() > 0){
					v = q.peek();
					if(v.timeout <= timeout){
						q.poll();
						elementCount--;
					}else break;
				}
			}
		}
	}

}
