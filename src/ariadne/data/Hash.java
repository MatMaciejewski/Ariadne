package ariadne.data;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import ariadne.utils.Hexadecimal;

public class Hash implements Comparable<Hash> {
	public final static int LENGTH = 16;
	private ByteBuffer data;

	public Hash(String s) {
		byte[] hash = Hexadecimal.fromString(s);
		assert (hash.length == LENGTH);
		data = ByteBuffer.allocate(LENGTH).put(hash);
	}

	public Hash(ByteBuffer b, int offset) {
		b.position(offset);
		data = ByteBuffer.allocate(LENGTH);
		for (int i = 0; i < LENGTH; ++i)
			data.put(b.get());
	}

	public Hash(byte[] b, int offset) {
		data = ByteBuffer.allocate(LENGTH).put(b, offset, LENGTH);
	}

	public ByteBuffer getByteBuffer() {
		ByteBuffer b = data.asReadOnlyBuffer();
		b.rewind();
		return b;
	}

	public String toString() {
		return Hexadecimal.toString(data.array());
	}

	public static Hash computeFromString(String s) {
		byte[] bt = s.getBytes();
		return computeFromBytes(bt, 0, bt.length);
	}

	public static Hash computeFromByteBuffer(ByteBuffer b) {
		return computeFromByteBuffer(b, 0, b.capacity());
	}

	public static Hash computeFromByteBuffer(ByteBuffer b, int offset,
			int length) {
		byte[] bt = new byte[length];
		b.position(offset);
		b.get(bt, 0, length);
		return computeFromBytes(bt, 0, length);
	}

	public static Hash computeFromBytes(byte[] b, int offset, int length) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(b, offset, length);
			byte[] result = md.digest();
			assert (result.length == LENGTH);
			return new Hash(result, 0);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public int compareTo(Hash o) {
		// required for queues
		int r;
		data.rewind();
		o.data.rewind();
		for (int i = 0; i < LENGTH; ++i) {
			r = data.get() - o.data.get();
			if (r != 0)
				return r;
		}
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		// required for hashmaps
		if (o instanceof Hash) {
			Hash h = (Hash) o;
			return (compareTo(h) == 0);
		} else
			return false;
	}

	@Override
	public int hashCode() {
		// required for hashmaps
		return toString().hashCode();
	}
}
