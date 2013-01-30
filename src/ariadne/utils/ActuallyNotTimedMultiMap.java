package ariadne.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActuallyNotTimedMultiMap<K, V> implements TimedMultiMap<K, V> {
	private Map<K, Set<V>> data;
	private int size = 0;

	public ActuallyNotTimedMultiMap() {
		data = new HashMap<K, Set<V>>();
	}

	private Set<V> getVals(K key) {
		Set<V> vals = data.get(key);
		if (vals == null) {
			vals = new HashSet<V>();
			data.put(key, vals);
		}
		return vals;
	}

	@Override
	public void add(K key, V value, long timeout) {
		Set<V> vals = getVals(key);
		if (vals.add(value)){
			System.out.println("Added!");
			size++;
		}else{
			System.out.println("Not added");
		}
	}

	@Override
	public List<V> get(K key, int count) {
		List<V> ret = new LinkedList<V>();
		LinkedList<V> l = new LinkedList<V>();
		Set<V> vals = getVals(key);
		
		l.addAll(vals);
		Collections.shuffle(l);
		
		Iterator<V> i = l.iterator();
		int c = 0;
		while(i.hasNext() && c < count){
			ret.add(i.next());
			c++;	
		}
		return ret;
	}

	@Override
	public List<V> getRandom(int count) {
		List<V> ret = new LinkedList<V>();
		LinkedList<V> allvals = new LinkedList<V>();
		Set<V> vals;
		Set<K> keys = data.keySet();
		for(K key: keys){
			vals = getVals(key);
			allvals.addAll(vals);
		}
		Collections.shuffle(allvals);
		Iterator<V> i = allvals.iterator();
		int c = 0;
		while(i.hasNext() && c < count){
			ret.add(i.next());
			c++;	
		}
		return ret;
	}

	@Override
	public void removeTimeouted(long timeout) {
		// TODO Auto-generated method stub

	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public void remove(K key, V value) {
		Set<V> vals = data.get(key);
		if (vals != null) {
			if (vals.remove(value))
				size--;
			if (vals.size() == 0) {
				data.remove(key);
			}
		}
	}
}
