package ariadne.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import ariadne.utils.DiskResource;
import ariadne.utils.Log;

public class BetterSettings {

	public static final String SETTINGS_FILENAME = "./.ariadne_settings";
	public static final String SETTINGS_COMMENTS = "The Ariadne settings file";
	private static Properties data;

	public static void init() {
		data = new Properties();
	}

	public static String getFileName(Hash hash) {
		String txt = (String) data.get(hash.toString());
		java.io.File f = new java.io.File(txt);
		return f.getName();
	}

	public static String getFilePath(Hash hash) {
		String txt = (String) data.get(hash.toString());
		java.io.File f = new java.io.File(txt);
		return f.getParent();
	}

	public static void add(Hash hash, String fileName, String filePath) {
		if (!data.contains(hash.toString())) {
			data.put(hash.toString(), filePath + "/" + fileName);
		}
	}

	public static void remove(Hash hash) {
		data.remove(hash.toString());
	}

	public static void load() {
		try {
			ByteBuffer b = DiskResource.getFileContents(SETTINGS_FILENAME);
			if (b != null) {
				ByteArrayInputStream in = new ByteArrayInputStream(b.array());
				data.load(in);
			} else {
				throw new IOException();
			}
		} catch (Exception e) {
			Log.warning("Could not load the settings file.");
		}
	}

	public static void save() {
		try {			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			data.store(out, SETTINGS_COMMENTS);
			if (!DiskResource.putFileContents(out.toByteArray(), 0, out.size(), SETTINGS_FILENAME)) {
				throw new IOException();
			}
		} catch (IOException e) {
			Log.warning("Could not save the settings file.");
		}
	}

	public static List<Hash> getKeys() {
		List<Hash> l = new LinkedList<Hash>();
		for (Object k : data.keySet()) {
			try {
				Hash h = new Hash((String) k);
				if(h != null){
					l.add(h);
				}
			} catch (Exception e) {
				data.remove(k);
			}
		}
		return l;
	}
}
