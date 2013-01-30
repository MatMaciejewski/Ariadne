package ariadne;

import java.util.Set;

import ariadne.data.Hash;

public interface TaskManager {
	public class TaskState{
		public Hash hash;
		public String path;
		public String name;
		public boolean knownDescriptor;
		public long fileSize;
		public long chunksPosessed;
		public int chunkCount;
		public int chunkSize;
	}
	
	
	public void insertTask(Hash hash, String path, String name);
	public void insertSeedTask(String path, String name, int chunkSize);
	public void removeTask(Hash hash);
	
	
	public Set<Hash> getTasks();
	public void closeAllTasks();
	public TaskState getTaskState(Hash hash);
}
