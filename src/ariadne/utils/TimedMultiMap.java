package ariadne.utils;

import java.util.List;

public interface TimedMultiMap<K,V> {
	public void add(K key, V value, long timeout);
	public void remove(K key, V value);
	public List<V> get(K key, int count);
	public List<V> getRandom(int count);
	public int size();
	public void removeTimeouted(long timeout);
}
