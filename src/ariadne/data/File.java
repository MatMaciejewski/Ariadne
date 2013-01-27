package ariadne.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import ariadne.utils.Log;

public class File {
	private Descriptor descriptor;
	private BitMask bitmask;
	private String fileName;

	public File(Descriptor descriptor, BitMask bitmask, String fileName) {
		this.descriptor = descriptor;
		this.fileName = fileName;
		this.bitmask = bitmask;
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
				saveChunkToDisk(chunk, id);
				return true;
			}
		}
		return false;
	}

	public String getFileName() {
		return fileName;
	}

	public BitMask getBitMask() {
		return bitmask;
	}

	public void removeFile() {
		java.io.File in = new java.io.File(fileName);
		in.delete();
	}

	// //////////////////////////////////////////

	private Chunk getChunkFromDisk(int id) {
		java.io.File in = new java.io.File(fileName);
		byte[] bytes = new byte[descriptor.getChunkSize()];
		RandomAccessFile byteFile = null;
		try {
			byteFile = new RandomAccessFile(in, "r");
		} catch (FileNotFoundException e1) {
			Log.error("File "+fileName+" not found.");
		}
		if (descriptor.getChunkSize() * id < in.length()) {
			try {
				byteFile.seek((descriptor.getChunkSize() + 1) * id);
				byteFile.read(bytes);
				System.out.println(new String(bytes));
				return new Chunk(bytes);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void saveChunkToDisk(Chunk c, int id) {
		java.io.File in = new java.io.File(fileName);
		RandomAccessFile byteFile = null;
		try {
			byteFile = new RandomAccessFile(in, "rws");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		int startingPosition = (descriptor.getChunkSize() + 1) * id;
		try {
			byteFile.seek(startingPosition);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			byteFile.write(c.getByteBuffer().array());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			byteFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
