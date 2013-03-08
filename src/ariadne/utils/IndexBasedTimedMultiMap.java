package ariadne.utils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class IndexBasedTimedMultiMap<K, V> implements TimedMultiMap<K, V>{
	private class Pair{
		public K k;
		public V v;
		@Override
		public int hashCode(){
			return 17*k.hashCode() + 31*v.hashCode();
		}
		
		@Override
		public boolean equals(Object o){
			if(o instanceof IndexBasedTimedMultiMap<?,?>.Pair){
				Pair p = (Pair) o;
				return k.equals(p.k) && v.equals(p.v);
			}
			return false;
		}
	}
	
	private Index<K, Integer> kIndex;			//used for finding rows by key
	private Index<V, Integer> vIndex;			//used for finding rows by value
	private RangeIndex<Integer, Long> eIndex;	//used for removing old rows
	private Map<Integer, Pair> data;			//used for data storage
	private Map<Pair, Integer> pids;			//used to detect (and prevent) duplicate pairs
	private int nextId = 0;
	
	public IndexBasedTimedMultiMap(){
		kIndex = new OrganizedIndex<K, Integer>();
		vIndex = new OrganizedIndex<V, Integer>();
		eIndex = new OrganizedRangeIndex<Integer, Long>();
		data = new HashMap<Integer, Pair>(); 
		pids = new HashMap<Pair, Integer>();
	}
	
	@Override
	public void insert(K key, V value, long expiration) {
		Pair q = new Pair();
		q.k = key;
		q.v = value;
		Integer id = pids.get(q);
		if(id == null){
			id = nextId++;
			data.put(id, q);
			pids.put(q, id);
			kIndex.insert(key, id);
			vIndex.insert(value, id);
		}else{
			eIndex.delete(id);
		}
		eIndex.insert(id, expiration);
	}

	@Override
	public Set<V> select(K key) {
		Set<V> vals = new HashSet<V>();
		for(Integer i: kIndex.get(key)){
			Pair q = data.get(i);
			vals.add(q.v);
		}
		return vals;
	}

	@Override
	public void update(long timestamp) {
		Set<Integer> keys = eIndex.selectBetween((long)0, timestamp);
		for(Integer i: keys){
			Pair q = data.remove(i);
			pids.remove(q);
			kIndex.delete(q.k, i);
			vIndex.delete(q.v, i);
			eIndex.delete(i);
		}
	}
	
	@Override
	public void deleteByKey(K key) {
		for(Integer i: kIndex.get(key)){
			Pair q = data.remove(i);
			pids.remove(q);
			vIndex.delete(q.v, i);
			eIndex.delete(i);
		}
		kIndex.deleteAll(key);
	}

	@Override
	public void deleteByValue(V value) {
		for(Integer i: vIndex.get(value)){
			Pair q = data.remove(i);
			pids.remove(q);
			kIndex.delete(q.k, i);
			eIndex.delete(i);
		}
		vIndex.deleteAll(value);
	}

}
