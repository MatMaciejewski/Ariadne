package ariadne.data;

import java.util.HashSet;


public class Database {
	private static Database files;
	private static HashSet<File> fileCollection;

	private Database() {
		fileCollection = new HashSet<File>();
	}

	public Database getDatabase() {
		if (files == null) {
			files = new Database();
		}
		return files;
	}

	/*public void initialize() {
		
	}

	public void finalize() {

	}*/

	public File getFile(Hash hash) {
		for(File target : fileCollection){
			if (target.getDescriptor().getHash()==hash) return target;
		}
		return null;
	}

	public void insertFile(Descriptor desc, BitMask bit, String fileName) {
		fileCollection.add(new File(desc,bit,fileName));
	}

	public void removeFile(Hash hash, boolean removeFromDisk) {
		for(File target : fileCollection){
			if (target.getDescriptor().getHash()==hash) fileCollection.remove(target);
			if(removeFromDisk==true)target.removeFile();
		}
		
	}

}
