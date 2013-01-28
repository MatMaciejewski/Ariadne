package ariadne.data;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import ariadne.utils.Log;

public class BitMask {
	private ByteBuffer mask;
	private int size;

	public BitMask(ByteBuffer b, int size) {
		if (size < 0)
			throw new IllegalArgumentException();

		this.size = size;
		int s = bytesRequiredForSize(size);
		mask = ByteBuffer.allocate(s);

		b.rewind();
		for (int i = 0; i < mask.capacity(); ++i) {
			mask.put(b.get());
		}
	}

	/**
	 * Create new bitmask, filled with 0
	 * 
	 * @param size
	 *            chunkCount
	 */
	public BitMask(int size) {
		if (size < 0)
			throw new IllegalArgumentException();

		this.size = size;
		int s = bytesRequiredForSize(size);
		mask = ByteBuffer.allocate(s);

		for (int i = 0; i < mask.capacity(); ++i) {
			mask.put((byte) 0);
		}
	}

	public BitMask getDiff(BitMask peer) {
		BitMask difference = new BitMask(getSize());
		if (getSize() != peer.getSize())
			throw new IllegalArgumentException();
		for (int i = 0; i < getSize(); i++)
			if (!get(i) && peer.get(i))
				difference.set(i);
		return difference;
	}

	/**
	 * Compare to an empty bitmask
	 * 
	 * @return true if it's all 0
	 */
	public boolean compareToNull() {
		for (int i = 0; i < size; i++)
			if (get(i) == true)
				return false;
		return true;
	}

	public boolean get(int id) {
		if (id < 0 || id >= getSize())
			throw new IllegalArgumentException();
		return (mask.get(id / 8) & (1 << id % 8)) > 0;
	}

	public void set(int id) {
		if (id < 0 || id >= getSize())
			throw new IllegalArgumentException();
		mask.put(id / 8, (byte) (mask.get(id / 8) | (1 << id % 8)));
	}

	public int getSize() {
		return size;
	}

	public int getByteCount() {
		return mask.capacity();
	}

	public static int bytesRequiredForSize(int i) {
		int s = i / 8;
		if (s * 8 < i)
			++s;
		return s;
	}

	public ByteBuffer getByteBuffer() {
		return mask.asReadOnlyBuffer();
	}

	public void saveBitMask(String filePath) {
		try {
			java.io.File file = new java.io.File("." + filePath + ".bmask");
			file.delete();
			RandomAccessFile byteFile = new RandomAccessFile(new java.io.File(
					filePath), "rws");
			byteFile.write(mask.array());
			byteFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BitMask loadBitMask(String filePath) {
		byte[] bytes = null;
		try {
			java.io.File in = new java.io.File("." + filePath + ".bmask");
			RandomAccessFile byteFile = new RandomAccessFile(in, "r");
			bytes = new byte[(int) in.length()];
			byteFile.read(bytes);
			byteFile.close();
			ByteBuffer bb = ByteBuffer.wrap(bytes);
			System.out.println(bb);
			return new BitMask(bb, (int) in.length());
		} catch (IOException e1) {
			Log.error("File " + filePath + " not found.");
			throw new IllegalArgumentException();
		}
	}
}
