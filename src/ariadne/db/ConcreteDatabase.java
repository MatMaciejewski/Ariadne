package ariadne.db;

import java.util.HashMap;
import java.util.Map;

import ariadne.Debug;
import ariadne.data.BitMask;
import ariadne.data.Chunk;
import ariadne.data.Descriptor;
import ariadne.data.Hash;

public class ConcreteDatabase implements Database {
	public static final String WORKDIR = System.getProperty("user.home")
			+ "/.ariadne/";
	private ResourceManager r;
	private Map<Hash, Descriptor> descriptors;
	private Map<Hash, BitMask> bitmasks;

	public ConcreteDatabase() {
		descriptors = new HashMap<Hash, Descriptor>();
		bitmasks = new HashMap<Hash, BitMask>();
		r = new ConcreteResourceManager();
		r.createFolder(WORKDIR);
		init();
	}

	private void init() {
		java.io.File f = new java.io.File(WORKDIR);
		String[] d = f.list();
		for (String s : d) {
			if (s.endsWith(".desc")) {
				try{
					Hash h = Hash.fromString(s.substring(0, s.length()-5));
					Descriptor desc = r.getDescriptor(WORKDIR + s);
					if(h.equals(desc.getHash())){
						descriptors.put(h, desc);
					}else{
						throw new IllegalArgumentException();
					}
				}catch(IllegalArgumentException e){
					Debug.warn("Invalid .desc file ("+s+")");
				}
				
			}
			else if (s.endsWith(".mask")) {
				try{
				Hash h = Hash.fromString(s.substring(0, s.length()-5));
				BitMask b = r.getBitMask(WORKDIR+s);
				System.out.println(b);
				bitmasks.put(h, b);
				}catch(IllegalArgumentException e){
					Debug.warn("Invalid .mask file ("+s+")");
				}
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
		r.saveDescriptor(WORKDIR + h + ".desc", d);
		descriptors.put(h, d);
	}

	@Override
	public void removeDescriptor(Hash h) {
		r.removeResource(WORKDIR + h + ".desc");
		descriptors.remove(h);
	}

	@Override
	public BitMask getBitMask(Hash h) {
		return bitmasks.get(h);
	}

	@Override
	public void addBitMask(Hash h, BitMask b) {
		r.saveBitMask(WORKDIR + h + ".mask", b);
		bitmasks.put(h, b);
	}

	@Override
	public void removeBitMask(Hash h) {
		r.removeResource(WORKDIR + h + ".mask");
		bitmasks.remove(h);
	}

	@Override
	public Chunk getChunk(String filepath, int id, int size) {
		return r.getChunk(filepath, id*size, size);
	}

	@Override
	public void setChunk(String filepath, int id, Chunk c) {
		r.saveChunk(filepath, id*c.size(), c);
	}
}
