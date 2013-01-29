package ariadne.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import ariadne.utils.Log;

public class BitMask {
	private ByteBuffer mask;
	private int size;
	private int posessed;

	public BitMask(ByteBuffer b, int offset, int size) {
		if (size < 0)
			throw new IllegalArgumentException();

		this.size = size;
		int s = bytesRequiredForSize(size);
		mask = ByteBuffer.allocate(s);

		b.rewind();
		b.position(offset);
		for (int i = 0; i < mask.capacity(); ++i) {
			mask.put(b.get());
		}
		for (int i = 0; i < size; i++)
			if (get(i)) posessed++;
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
		posessed = 0;
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
		if(get(id)) return;
		if (id < 0 || id >= getSize())
			throw new IllegalArgumentException();
		mask.put(id / 8, (byte) (mask.get(id / 8) | (1 << id % 8)));
		posessed++;
	}

	public int getSize() {
		return size;
	}
	
	public int getPosessed(){
		return posessed;
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
		ByteBuffer b = mask.asReadOnlyBuffer();
		b.rewind();
		return b;
	}

	public void saveBitMask(String fileName) {
		try {
			java.io.File file = new java.io.File(fileName);
			file.delete();
			RandomAccessFile byteFile = new RandomAccessFile(new java.io.File(
					fileName), "rws");
			byteFile.write(mask.array());
			byteFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static BitMask loadBitMask(String filePath, int chunkCount){
		try {
			int byteLength = BitMask.bytesRequiredForSize(chunkCount);
			RandomAccessFile byteFile = new RandomAccessFile(new java.io.File(filePath), "r");
			byte[] bytes = new byte[byteLength];
			
			byteFile.read(bytes);
			byteFile.close();
			
			ByteBuffer bb = ByteBuffer.allocate(byteLength);
			bb.put(bytes);
			
			return new BitMask(bb, 0, chunkCount);
		} catch (FileNotFoundException e) {
		} catch (IOException e) { }
		return null;
	}
}
