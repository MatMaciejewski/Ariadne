package ariadne.utils;

import java.util.Set;

/**
 * @author eipifi Interface for the multimap that keeps track of when elements
 *         should be removed automatically.
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public interface TimedMultiMap<K, V> {

	/**
	 * Inserts a given key/value pair with the specified expiration date.
	 * 
	 * @param key
	 *            key
	 * @param value
	 *            value to be stored
	 * @param expiration
	 *            when the element whould be removed
	 */
	public void insert(K key, V value, long expiration);

	/**
	 * Selects all values the given key corresponds to.
	 * 
	 * @param key
	 *            key to be used to perform search
	 * @return set of all values the key corresponds to
	 */
	public Set<V> select(K key);

	/**
	 * Removes all the entries with a given element as value
	 * 
	 * @param value
	 *            value to be removed
	 */
	public void deleteByValue(V value);
	
	/**
	 * Removes all the entries with a given element as key
	 * 
	 * @param key
	 *            key to be removed
	 */
	public void deleteByKey(K key);

	/**
	 * Updates the timestamp and removes all the elements whose expiration is
	 * less than of equal to the given timestamp
	 * 
	 * @param timestamp
	 *            current time
	 */
	public void update(long timestamp);
}
