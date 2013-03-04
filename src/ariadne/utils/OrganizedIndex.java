package ariadne.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OrganizedIndex<K, V> implements Index<K, V>{
	private Map<K, Set<V>> data;
	public OrganizedIndex(){
		data = new HashMap<K, Set<V>>();
	}
	
	@Override
	public Set<V> get(K key) {
		Set<V> v = data.get(key);
		if(v == null){
			v = Collections.emptySet();
		}
		return v;
	}

	@Override
	public void insert(K key, V value) {
		Set<V> v = data.get(key);
		if(v == null){
			v = new HashSet<V>();
			data.put(key, v);
		}
		v.add(value);
	}

	@Override
	public void delete(K key, V value) {
		Set<V> v = data.get(key);
		if(v != null){
			v.remove(value);
		}
	}

	@Override
	public void deleteAll(K key) {
		data.remove(key);
	}
}
