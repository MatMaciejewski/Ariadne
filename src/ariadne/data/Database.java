package ariadne.data;

import java.util.HashMap;

public class Database {
	private static HashMap<Hash, File> files;

	private Database() {
		files = new HashMap<Hash, File>();
	}

	public static File getFile(Hash hash) {
		return files.get(hash);
	}

	public static void insertFile(Descriptor desc, BitMask bit, String fileName) {
		files.put(desc.getHash(), new File(desc, bit, fileName));
	}

	public static void removeFile(Hash hash, boolean removeFromDisk) {
		File f = files.remove(hash);
		if (removeFromDisk && f != null)
			f.removeFile();
	}
}
