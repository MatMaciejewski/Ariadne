package ariadne.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashSet;

import ariadne.utils.Log;

/*
 * Byte 0-3		- Chunk count
 * Byte 4-7		- Chunk size in bytes
 * Byte 8-*		- Chunk hashes
 */

public class Descriptor {
	public final static int SMALLEST_ALLOWED_CHUNK_SIZE = Hash.LENGTH;
	private ByteBuffer data;
	
	public Descriptor(int chunkSize, HashSet<Hash> hashes) {
		if ((chunkSize < SMALLEST_ALLOWED_CHUNK_SIZE) || (hashes == null)
				|| (hashes.size() == 0))
			throw new IllegalArgumentException();

		data = ByteBuffer.allocate(8 + Hash.LENGTH * hashes.size());

		data.rewind();
		data.putInt(hashes.size());
		data.putInt(chunkSize);
		for (Hash h : hashes) {
			data.put(h.getByteBuffer());
		}
	}

	public static Descriptor parseFile(String filePath) {
		byte[] bytes = null;
		try {
			java.io.File in = new java.io.File(filePath);
			RandomAccessFile byteFile = new RandomAccessFile(in, "r");
			bytes = new byte[(int)in.length()];
			byteFile.read(bytes);
			byteFile.close();
		} catch (IOException e1) {
			Log.error("File "+filePath+" not found.");
			throw new IllegalArgumentException();
		}
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		System.out.println(bb);
		return parse(bb, 0);
	}

	public BitMask getEmptyBitmask(){
		return new BitMask(getChunkCount());
	}
	
	public Hash getHash() {
		return Hash.computeFromByteBuffer(data);
	}

	public Hash getChunkHash(int id) {
		if (id < 0 || id >= getChunkCount())
			throw new IllegalArgumentException();
		return new Hash(data, 8 + Hash.LENGTH * id);
	}

	public int getChunkSize() {
		data.position(4);
		return data.getInt();
	}

	public int getChunkCount() {
		data.position(0);
		return data.getInt();
	}

	public ByteBuffer getByteBuffer() {
		return data;
	}

	private Descriptor() {
	}

	public static Descriptor parse(ByteBuffer b, int offset) {
		try {
			b.position(offset);
			int chunkCount = b.getInt();
			int chunkSize = b.getInt();
			if ((chunkSize < SMALLEST_ALLOWED_CHUNK_SIZE) || (chunkCount <= 0)
					|| (b.remaining() < Hash.LENGTH * chunkCount))
				throw new IllegalArgumentException();

			Descriptor d = new Descriptor();
			d.data = ByteBuffer.allocate(8 + Hash.LENGTH * chunkCount);
			d.data.putInt(chunkCount);
			d.data.putInt(chunkSize);
			b.position(offset);
			for (int i = Hash.LENGTH * chunkCount; i > 0; --i) {
				d.data.put(b.get());
			}

			return d;
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException();
		}
	}

	public void saveDescriptor(String filePath) {
		try {
			java.io.File file = new java.io.File("."+filePath+".desc");
			file.delete();
			RandomAccessFile byteFile = new RandomAccessFile(new java.io.File(filePath), "rws");
			byteFile.write(data.array());
			byteFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
