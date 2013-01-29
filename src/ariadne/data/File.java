package ariadne.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

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

	public boolean reallocate() {
		bitmask = descriptor.getEmptyBitmask();
		java.io.File f = new java.io.File(path + "/" + name);
		f.delete();
		f = new java.io.File(path + "/" + name);
		RandomAccessFile ran;
		try {
			ran = new RandomAccessFile(f, "rws");
			for (int i = 0; i < descriptor.getFileSize(); ++i) {
				ran.writeByte(0);
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public Descriptor getDescriptor() {
		return descriptor;
	}

	public Chunk getChunk(int id) {
		if (getBitMask().get(id)) {
			return getChunkFromDisk(id, descriptor.getChunkSize(), descriptor
					.getChunkCount());
		} else
			return null;
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
	
	public String getFilePath(){
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
			if (id == descriptor.getChunkCount()) {
				byte[] bytes = new byte[descriptor.getChunkSize()];
				bytes = c.getByteBuffer().array();
				byte[] bytesNew = new byte[(int) descriptor.getFileSize()
						- (id - 1) * descriptor.getChunkSize()];
				for (int i = 0; i < (int) descriptor.getFileSize() - (id - 1)
						* descriptor.getChunkSize(); i++) {
					bytesNew[i] = bytes[i];
				}
				RandomAccessFile byteFile = new RandomAccessFile(
						new java.io.File(path + "/" + name), "rws");
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

	// ///////////////CREATE A FILE//////////////////
	public File(String path, String name, int chunkSize) {
		java.io.File fil = new java.io.File(path + "/" + name);
		this.path = path;
		this.name = name;
		System.out.println(path+"/"+name);
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
			System.out.println("FL: "+fileLength+" FS: "+fil.length());
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
			bit.saveBitMask(path + "/" + name + ".bmask");
			d.saveDescriptor(path + "/" + name + ".desc");
			System.out.println(d.getChunkCount());
			System.out.println(bit.getSize());
			System.out.println(path);
			System.out.println(name);
			Database.insertFile(d, bit, path, name, false);
		}
	}

	// ///////////////LOAD A FILE//////////////////

	public static void loadReadyFile(String path, String name) {
		java.io.File testFile = new java.io.File(path + "/" + name);
		if (testFile != null) {
			Descriptor desc = Descriptor.parseFile(path + "/" + name);
			BitMask bit = BitMask.loadBitMask(path + "/" + name);
			if (desc != null && bit != null) {
				Database.insertFile(desc, bit, path, name, false);
			} else
				Log.error("File Descriptor\bitmask not found : " + path + "/"
						+ name);
		}

	}
}
