package ariadne.data;

public interface Database {
	public void initialize();
	public void finalize();
	
	
	public File getFile(Hash hash);
	public void insertFile(Descriptor d);
	public void removeFile(Hash h, boolean remove_from_disk);
	
	
}
