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
		if (descriptor == null || bitmask == null || path == null
				|| name == null)
			throw new IllegalArgumentException();
		this.descriptor = descriptor;
		this.bitmask = bitmask;
		this.path = path;
		this.name = name;

	}

	public Descriptor getDescriptor() {
		return descriptor;
	}

	public Chunk getChunk(int id) {
		if(getBitMask().get(id)){
			return getChunkFromDisk(id);	
		}else return null;
	}

	public boolean setChunk(Chunk chunk, int id) {
		if (chunk.getSize() == descriptor.getChunkSize()) {
			if (chunk.getHash() == descriptor.getChunkHash(id)) {
				if (saveChunkToDisk(chunk, id)) {
					bitmask.set(id);
					return true;
				} else
					return false;

			} else
				Log.error("Chunk's hash != descriptor's hash!");
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
			if (id == descriptor.getChunkCount()) {
				if (bytes.length < descriptor.getChunkSize()) {
					for (int i = bytes.length; i < descriptor.getChunkSize(); i++) {
						bytes[i] = (byte) 0;
					}
				}
			}
			return new Chunk(bytes);
		} catch (IOException e1) {
			Log.error("File " + name + " not found.");
		}
		return null;
	}

	private boolean saveChunkToDisk(Chunk c, int id) {
		try {
			if (id == descriptor.getChunkCount()) {
				byte[] bytes = new byte[descriptor.getChunkSize()];
				bytes = c.getByteBuffer().array();
				byte[] bytesNew = new byte[(int) descriptor.getFileSize()
						- (id - 1) * descriptor.getChunkSize()];
				for (int i = 0; i < (int) descriptor.getFileSize()
						- (id - 1) * descriptor.getChunkSize(); i++) {
						bytesNew[i] = bytes[i];
					}
				RandomAccessFile byteFile = new RandomAccessFile(new java.io.File(
						path + "/" + name), "rws");
				int startingPosition = (descriptor.getChunkSize() + 1) * id;
				byteFile.seek(startingPosition);
				byteFile.write(bytesNew);
				bitmask.set(id);
				byteFile.close();
				return true;
			
				}
			
			RandomAccessFile byteFile = new RandomAccessFile(new java.io.File(
					path + "/" + name), "rws");
			int startingPosition = (descriptor.getChunkSize() + 1) * id;
			byteFile.seek(startingPosition);
			byteFile.write(c.getByteBuffer().array());
			bitmask.set(id);
			byteFile.close();
			return true;
		} catch (IOException e) {
			Log.error("Error while accessing file.");
		}
		return false;
	}
}
