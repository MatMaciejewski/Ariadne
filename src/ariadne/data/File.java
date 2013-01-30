package ariadne.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import ariadne.Application;
import ariadne.utils.DiskResource;
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

		if (descriptor.getChunkCount() != bitmask.getSize())
			throw new IllegalArgumentException();

		this.descriptor = descriptor;
		this.bitmask = bitmask;
		this.path = path;
		this.name = name;

	}

	public String getDefaultFileName() {
		return path + "/" + name;
	}

	/**
	 * Clears the bitmask and reallocates disk memory
	 */
	public boolean reallocate() {
		DiskResource d = DiskResource.open(getDefaultFileName(), true);
		if (d != null) {
			if (d.resize(getDescriptor().getFileSize())) {
				d.close();
				this.bitmask = getDescriptor().getEmptyBitmask();
				return true;
			}
		}
		d.close();
		return false;
	}

	public boolean isAllocated() {
		DiskResource d = DiskResource.open(getDefaultFileName(), true);
		if (d != null) {
			if (d.getLength() == getDescriptor().getFileSize()) {
				d.close();
				return true;
			}
		}
		d.close();
		return false;
	}

	public Descriptor getDescriptor() {
		return descriptor;
	}

	public Chunk getChunk(int id) {
		if (getBitMask().isSet(id)) {
			return getChunkFromDisk(id, descriptor.getChunkSize(), descriptor
					.getChunkCount());
		} else
			return null;
	}

	public boolean setChunk(Chunk chunk, int id) {
		if (chunk.getSize() == descriptor.getChunkSize()) {
			if (chunk.getHash().equals(descriptor.getChunkHash(id))) {
				if (saveChunkToDisk(chunk, id)) {
					bitmask.set(id);
					return true;
				} else
					return false;

			} else {
				Log.error("Chunk's hash != descriptor's hash!");
			}
		}
		return false;
	}

	public String getFileName() {
		return name;
	}

	public String getFilePath() {
		return path;
	}

	public BitMask getBitMask() {
		return bitmask;
	}

	public void removeFile() {
		java.io.File in = new java.io.File(name);
		in.delete();
	}

	// //////////////////////////////////////////

	private Chunk getChunkFromDisk(int id, int chunkSize, int chunkCount) {
		byte[] bytes = new byte[chunkSize];
		try {
			java.io.File in = new java.io.File(path + "/" + name);
			RandomAccessFile byteFile = new RandomAccessFile(in, "r");
			byteFile.seek(chunkSize * id);
			byteFile.read(bytes);
			byteFile.close();
			if (id == chunkCount) {
				if (bytes.length < chunkSize) {
					for (int i = bytes.length; i < chunkSize; i++) {
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
			int toWrite = descriptor.getChunkSize();
			if (id + 1 == descriptor.getChunkCount()) {
				toWrite -= (descriptor.getChunkCount()
						* descriptor.getChunkSize() - descriptor.getFileSize());
			}
			byte[] bytes = new byte[toWrite];
			ByteBuffer b = c.getByteBuffer();
			b.get(bytes);
			RandomAccessFile byteFile = new RandomAccessFile(new java.io.File(
					path + "/" + name), "rws");
			int pos = id * descriptor.getChunkSize();
			byteFile.seek(pos);
			byteFile.write(bytes);
			bitmask.set(id);
			byteFile.close();
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static File prepareExistingOne(String path, String name, int chunkSize){
		
		return null;
	}

	public File(String path, String name, int chunkSize) {
		java.io.File fil = new java.io.File(path + "/" + name);
		this.path = path;
		this.name = name;
		System.out.println(path + "/" + name);
		if (fil != null) {
			ByteBuffer b;
			int fileLength = (int) fil.length() / chunkSize;
			b = ByteBuffer.allocate(4 + 4 + 8 + Hash.LENGTH
					* BitMask.bytesRequiredForSize((int) fil.length()));
			if ((int) fil.length() % chunkSize != 0)
				fileLength++;
			b.putInt(fileLength);
			b.putInt(chunkSize);
			b.putLong(fil.length());
			for (int i = 0; i < fileLength; i++) {
				Hash temp = getChunkFromDisk(i, chunkSize, fileLength)
						.getHash();
				b.put(temp.getByteBuffer());
			}
			Descriptor d = Descriptor.parse(b, 0);
			descriptor = d;
			BitMask bit = d.getEmptyBitmask();
			for (int i = 0; i < bit.getSize(); i++)
				bit.set(i);
			bitmask = bit;
			bit.saveToFile(BitMask.getDefaultFileName(path, name));
			d.saveToFile(Descriptor.getDefaultFileName(path, name));
			
			File f = new File(d, bit, path, name);
			Database.insertFile(f);
		}
	}
}
