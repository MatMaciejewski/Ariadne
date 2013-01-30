package ariadne;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ariadne.data.File;
import ariadne.data.Hash;
import ariadne.data.Settings;
import ariadne.utils.Log;

public class Manager implements TaskManager {
	private Map<Hash, Supervisor> threads;

	public Manager() {
		threads = new HashMap<Hash, Supervisor>(); //This should be concurrent
	}

	@Override
	public TaskState getTaskState(Hash hash) {
		TaskState t = new TaskState();
		Supervisor s = threads.get(hash);
		if (s == null)
			throw new IllegalArgumentException();
		t.hash = hash;
		t.name = s.getFileName();
		t.path = s.getFilePath();
		t.kbytesPosessed = s.getPosessed();
		t.kbytesSize = s.getSize();
		t.knownDescriptor = s.knownDescriptor();
		return t;
	}

	@Override
	public Set<Hash> getTasks() {
		return threads.keySet();
	}

	public void closeAllTasks() {
		try{
		Set<Hash> keys = getTasks();

		for (Hash h : keys) {
			removeTask(h);
		}
		}catch(ConcurrentModificationException e){
			Log.error("Again, problems with the hashmap in manager");		}
	}

	@Override
	public void insertTask(Hash hash, String path, String name) {
		if (threads.get(hash) == null) {
			Supervisor s = Supervisor.forPartialFile(hash, path, name);
			if(s != null){
				threads.put(hash, s);
				Settings.add(hash, name, path);
				s.start();
			}else{
				Log.notice("Could not insert a task for hash="+hash.toString());
			}
		}
	}

	@Override
	public void removeTask(Hash hash) {
		Supervisor s = threads.remove(hash);
		if (s == null)
			throw new IllegalArgumentException();
		s.halt();
	}

	@Override
	public void insertSeedTask(String path, String name, int chunkSize) {
		
		//TODO: fix this
		File f = new File(path, name, chunkSize);
		Hash hash = f.getDescriptor().getHash();
		
		if (threads.get(hash) == null) {
			Supervisor s = Supervisor.forCompleteFile(f);
			if(s != null){
				threads.put(hash, s);
				Settings.add(hash, name, path);
				s.start();
			}else{
				Log.notice("Could not insert a task for hash="+hash.toString());
			}
		}
	}

}
