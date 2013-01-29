package ariadne;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ariadne.data.File;
import ariadne.data.Hash;

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
		t.knownDescriptor = s.knownDescriptor();
		return t;
	}

	@Override
	public Set<Hash> getTasks() {
		return threads.keySet();
	}

	public void closeAllTasks() {
		Set<Hash> keys = getTasks();

		for (Hash h : keys) {
			removeTask(h);
		}
	}

	@Override
	public void insertTask(Hash hash, String path, String name) {
		if (threads.get(hash) == null) {
			Supervisor s = new Supervisor(hash, path, name);
			threads.put(hash, s);
			s.start();
		}
	}
	
	@Override
	public void insertTask(File file) {
		Hash h = file.getDescriptor().getHash();
		if (threads.get(h) == null) {
			Supervisor s = new Supervisor(file);
			threads.put(h, s);
			s.start();
		}
	}

	@Override
	public void removeTask(Hash hash) {
		Supervisor s = threads.remove(hash);
		if (s == null)
			throw new IllegalArgumentException();
		s.halt();
	}

}
