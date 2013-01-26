package ariadne.data;

import java.nio.ByteBuffer;

public class BitMask {
	private ByteBuffer mask;
	private int size;

	public BitMask(int size) {
		if (size < 0)
			throw new IllegalArgumentException();

		this.size = size;
		int s = size / 8;
		if (s * 8 < size)
			++s;

		mask = ByteBuffer.allocate(s);

		for (int i = 0; i < mask.capacity(); ++i) {
			mask.put((byte) 0);
		}
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

	public ByteBuffer getByteBuffer() {
		return mask.asReadOnlyBuffer();
	}
}
