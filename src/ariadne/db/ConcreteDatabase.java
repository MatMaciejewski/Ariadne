package ariadne.db;

import java.util.HashMap;
import java.util.Map;

import ariadne.data.Descriptor;
import ariadne.data.Hash;

public class ConcreteDatabase implements Database {
	public static final String WORKDIR = System.getProperty("user.home")
			+ "/.ariadne/";
	private ResourceManager r;
	private Map<Hash, Descriptor> descriptors;

	public ConcreteDatabase() {
		descriptors = new HashMap<Hash, Descriptor>();
		r = new ConcreteResourceManager();
		r.createFolder(WORKDIR);
		init();
	}

	private void init() {
		java.io.File f = new java.io.File(WORKDIR);
		String[] d = f.list();
		for (String s : d) {
			if (s.endsWith(".desc")) {
				Hash h = Hash.fromString(s.substring(0, s.length()-5));
				Descriptor desc = r.getDescriptor(WORKDIR + s);
				Hash h2 = desc.getHash();
				System.out.println(h);
				System.out.println(h2);
			}
		}
	}

	@Override
	public Descriptor getDescriptor(Hash h) {
		return descriptors.get(h);
	}

	@Override
	public void addDescriptor(Descriptor d) {
		Hash h = d.getHash();
		String path = WORKDIR + h + ".desc";
		r.saveDescriptor(path, d);
		descriptors.put(h, d);
	}

	@Override
	public void removeDescriptor(Hash h) {
		r.removeResource(WORKDIR + h + ".desc");
		descriptors.remove(h);
	}
}
