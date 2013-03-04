package ariadne.db;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ariadne.data.Address;
import ariadne.data.Hash;
import ariadne.utils.IndexBasedTimedMultiMap;
import ariadne.utils.TimedMultiMap;

public class Catalogue {
	private TimedMultiMap<Hash, Address> data;
	private ReadWriteLock rwl;
	private Lock r;
	private Lock w;
	
	public Catalogue(){
		data = new IndexBasedTimedMultiMap<Hash, Address>();
		rwl = new ReentrantReadWriteLock();
		r = rwl.readLock();
		w = rwl.writeLock();
	}
	
	public void add(Hash h, Address a, int timeout){
		w.lock();
		data.insert(h, a, System.currentTimeMillis()+timeout*1000);
		w.unlock();
	}
	
	public void removeHash(Hash h){
		w.lock();
		data.deleteByKey(h);
		w.unlock();
	}
	
	public void removeAddress(Address a){
		w.lock();
		data.deleteByValue(a);
		w.unlock();
	}
	
	public void refresh(){
		w.lock();
		data.update(System.currentTimeMillis());
		w.unlock();
	}
	
	public Set<Address> getAddresses(Hash h){
		Set<Address> ret;
		r.lock();
		ret = data.select(h);
		r.unlock();
		return ret;
	}
}
