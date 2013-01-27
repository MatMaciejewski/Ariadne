package ariadne.data;

import java.io.IOException;
import java.io.RandomAccessFile;
import ariadne.utils.Log;

public class File {
	private Descriptor descriptor;
	private BitMask bitmask;
	private String name;
	private String path;

	public File(Descriptor descriptor, BitMask bitmask, String path, String name) {
		this.descriptor = descriptor;
		this.bitmask = bitmask;
		this.path = path;
		this.name = name;

	}

	public Descriptor getDescriptor() {
		return descriptor;
	}

	public Chunk getChunk(int id) {
		return getChunkFromDisk(id);
	}

	public boolean setChunk(Chunk chunk, int id) {
		if (chunk.getSize() == descriptor.getChunkSize()) {
			if (chunk.getHash() == descriptor.getChunkHash(id)) {
				if (saveChunkToDisk(chunk, id)) {
					bitmask.set(id);
					return true;
				} else
					return false;

			}
		}
		return false;
	}

	public String getFileName() {
		return name;
	}

	public BitMask getBitMask() {
		return bitmask;
	}

	public void removeFile() {
		java.io.File in = new java.io.File(name);
		in.delete();
	}

	// //////////////////////////////////////////

	private Chunk getChunkFromDisk(int id) {
		byte[] bytes = new byte[descriptor.getChunkSize()];
		try {
			java.io.File in = new java.io.File(path + "/" + name);
			RandomAccessFile byteFile = new RandomAccessFile(in, "r");
			byteFile.seek((descriptor.getChunkSize() + 1) * id);
			byteFile.read(bytes);
			byteFile.close();
			return new Chunk(bytes);
		} catch (IOException e1) {
			Log.error("File " + name + " not found.");
		}
		return null;
	}

	private boolean saveChunkToDisk(Chunk c, int id) {
		try {
			RandomAccessFile byteFile = new RandomAccessFile(new java.io.File(
					path + "/" + name), "rws");
			int startingPosition = (descriptor.getChunkSize() + 1) * id;
			byteFile.seek(startingPosition);
			byteFile.write(c.getByteBuffer().array());
			byteFile.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
