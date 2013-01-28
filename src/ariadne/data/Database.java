package ariadne.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ariadne.utils.Log;

public class Database {
	private static Map<Hash, File> files;;

	public static void initialize() {
		files = new ConcurrentHashMap<Hash, File>();
	}

	public static File getFile(Hash hash) {
		return files.get(hash);
	}

	public static void insertFile(Descriptor desc, BitMask bit, String path, String name, boolean reallocate) {
		File f = new File(desc, bit, path, name);
		if(reallocate){
			if(!f.reallocate()){
				Log.error("Could not reallocate file " + name);
			}
		}
		files.put(desc.getHash(), f);
	}

	public static void removeFile(Hash hash, boolean removeFromDisk) {
		File f = files.remove(hash);
		if (removeFromDisk && f != null)
			f.removeFile();
	}
}
