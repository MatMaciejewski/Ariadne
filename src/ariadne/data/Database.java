package ariadne.data;

import java.util.HashMap;

public class Database {
	private static Database files;
	private static HashMap<Hash, File> fileCollection;

	private Database() {
		fileCollection = new HashMap<Hash, File>();
	}

	public Database getDatabase() {
		if (files == null) {
			files = new Database();
		}
		return files;
	}

	/*
	 * public void initialize() {
	 * 
	 * }
	 * 
	 * public void finalize() {
	 * 
	 * }
	 */

	public File getFile(Hash hash) {
		if (fileCollection.containsKey(hash))
			return fileCollection.get(hash);
		return null;
	}

	public void insertFile(Descriptor desc, BitMask bit, String fileName) {
		fileCollection.put(desc.getHash(), new File(desc, bit, fileName));
	}

	public void removeFile(Hash hash, boolean removeFromDisk) {
		if(removeFromDisk==true) if(fileCollection.containsKey(hash)) fileCollection.get(hash).removeFile();
		if(fileCollection.containsKey(hash)) fileCollection.remove(hash);
	}
}
