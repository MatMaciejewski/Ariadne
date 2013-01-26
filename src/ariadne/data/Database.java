package ariadne.data;

import java.util.HashMap;

public class Database {
	private static HashMap<Hash, File> fileCollection;

	private Database() {
		fileCollection = new HashMap<Hash, File>();
	}

	public static File getFile(Hash hash) {
		return fileCollection.get(hash);
	}

	public static void insertFile(Descriptor desc, BitMask bit, String fileName) {
		fileCollection.put(desc.getHash(), new File(desc, bit, fileName));
	}

	public static void removeFile(Hash hash, boolean removeFromDisk) {
		if (fileCollection.containsKey(hash)) {
			if (removeFromDisk)
				getFile(hash).removeFile();
			fileCollection.remove(hash);
		}
	}
}
