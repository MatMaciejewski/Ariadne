package ariadne.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

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
		System.out.println("SIZE: " + chunk.getSize() + "\nDEF SIZE: "
				+ descriptor.getChunkSize());
		if (chunk.getSize() == descriptor.getChunkSize()) {
			System.out.println("HASH: " + chunk.getHash() + "\nHASH DEF: "
					+ descriptor.getChunkHash(id));
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

	// //////////////////////////////////////////

	private Chunk getChunkFromDisk(int id) {
		// FileInputStream in = null;
		java.io.File in = new java.io.File(fileName);
		byte[] bytes = new byte[descriptor.getChunkSize()];
		RandomAccessFile byteFile = null;
		try {
			byteFile = new RandomAccessFile(in, "r");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (descriptor.getChunkSize() * id < in.length()) {
			try {
				byteFile.seek((descriptor.getChunkSize() + 1) * id);
				byteFile.read(bytes);
				System.out.println(new String(bytes));
				return new Chunk(bytes);

			} catch (IOException e) {
				// TODO Auto-generated catch block
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
		int startingPosition = (descriptor.getChunkSize()+1) * id;
		try {
			byteFile.seek(startingPosition);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			byteFile.write(c.getBytes());
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
