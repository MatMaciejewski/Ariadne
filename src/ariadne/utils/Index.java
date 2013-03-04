package ariadne.utils;

import java.util.Set;

public interface Index<K, V> {
	public void insert(K key, V value);
	public Set<V> get(K key);
	public void delete(K key, V value);
	public void deleteAll(K key);
}
