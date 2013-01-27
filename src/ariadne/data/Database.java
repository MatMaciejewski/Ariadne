package ariadne.data;

import java.util.HashMap;
import java.util.Map;

public class Database {
	private static Map<Hash, File> files;;

	public static void initialize() {
		files = new HashMap<Hash, File>();
	}

	public static File getFile(Hash hash) {
		return files.get(hash);
	}

	public static void insertFile(Descriptor desc, BitMask bit, String path, String name) {
		files.put(desc.getHash(), new File(desc, bit, path, name));
	}

	public static void removeFile(Hash hash, boolean removeFromDisk) {
		File f = files.remove(hash);
		if (removeFromDisk && f != null)
			f.removeFile();
	}
}
