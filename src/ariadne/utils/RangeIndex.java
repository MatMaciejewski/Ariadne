package ariadne.utils;

import java.util.Set;

public interface RangeIndex<K, V extends Comparable<V>> {
	public void insert(K key, V value);
	public void delete(K key);
	public V select(K key);
	public Set<K> selectBetween(V lowerBound, V upperBound);
}
