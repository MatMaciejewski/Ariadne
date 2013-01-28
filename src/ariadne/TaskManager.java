package ariadne;

import java.util.Set;

import ariadne.data.Hash;

public interface TaskManager {
	public class TaskState{
		public Hash hash;
		public String path;
		public String name;
		public boolean knownDescriptor;
		public int kbytesSize;
		public int kbytesPosessed;
		public int upRate;
		public int downRate;
	}
	
	
	public void insertTask(Hash hash, String path, String name);
	public void removeTask(Hash hash);
	
	
	public Set<Hash> getTasks();
	public TaskState getTaskState(Hash hash);
}
