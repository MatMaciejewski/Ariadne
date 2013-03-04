package ariadne.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class OrganizedRangeIndex<K, V extends Comparable<V>> implements RangeIndex<K, V>{
	private class Pair implements Comparable<Pair>{
		K k;
		V v;
		@Override
		public int compareTo(Pair p) {
			return v.compareTo(p.v);
		}
		@Override
		public int hashCode(){
			return v.hashCode();
		}
	}
	private Map<K, Pair> index;
	private TreeSet<Pair> data;
	
	public OrganizedRangeIndex(){
		data = new TreeSet<Pair>();
		index = new HashMap<K, Pair>();
	}
	
	@Override
	public void delete(K key) {
		Pair p = index.remove(key);
		if(p != null){
			data.remove(p);
		}
	}

	@Override
	public void insert(K key, V value) {
		Pair p = new Pair();
		p.k = key;
		p.v = value;
		index.put(key, p);
		data.add(p);
	}

	@Override
	public Set<K> selectBetween(V lowerBound, V upperBound) {
		Set<K> keys = new HashSet<K>();
		Pair min = new Pair();
		Pair max = new Pair();
		min.v = lowerBound;
		max.v = upperBound;
		for(Pair p: data.subSet(min, max)){
			keys.add(p.k);
		}
		return keys;
	}

	@Override
	public V select(K key) {
		Pair p = index.get(key);
		return p.v;
	}
}
