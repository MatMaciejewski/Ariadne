package ariadne.protocol;

import java.nio.ByteBuffer;

import ariadne.net.Address;

public abstract class Message {
	private ByteBuffer buf;
	private Address address = null;

	private void checkAllocation(int additionalBytes) {
		if (buf == null) {
			buf = ByteBuffer.allocate(64);
			buf.position(0);
			buf.flip();
		}
		if (buf.capacity() < additionalBytes + buf.limit()) {
			int s = buf.capacity();
			while (s < additionalBytes + buf.limit()) {
				s *= 2;
			}
			ByteBuffer nbuf = ByteBuffer.allocate(s);
			nbuf.position(0);
			buf.position(0);
			nbuf.put(buf);
			nbuf.limit(buf.limit());
			buf = nbuf;
		}
	}

	public void addBytes(byte[] b, int length) {
		checkAllocation(length);
		buf.limit(buf.limit() + length);
		buf.put(b, 0, length);
	}

	public void addByteBuffer(ByteBuffer b) {
		// we expect that b has position = 0, limit = size and c>=limit
		// also, buf has position=pos, limit = size and c>= limit

		int pos = b.position();
		checkAllocation(b.limit());
		b.position(pos);
		buf.limit(buf.limit() + b.limit());
		buf.put(b);
		b.position(pos);
	}

	public ByteBuffer getByteBuffer() {
		ByteBuffer b;
		if (buf == null) {
			b = ByteBuffer.allocate(1);
			b.position(0);
			b.limit(0);
		} else {
			b = buf.asReadOnlyBuffer();
			b.rewind();
		}
		return b;
	}

	public void print() {
		ByteBuffer b = getByteBuffer();
		System.out.println("MESSAGE " + this);
		for (int i = 0; i < b.limit(); ++i) {
			System.out.print((byte) b.get(i) + " ");
		}
		System.out.println("END");
	}

	protected void setByteBuffer(ByteBuffer b) {
		if (b.isReadOnly())
			throw new IllegalArgumentException();
		buf = b;
	}

	protected ByteBuffer accessByteBuffer() {
		return buf;
	}

	public Address getAuthor() {
		return address;
	}

	public void setAuthor(Address a) {
		if (address == null)
			address = a;
	}

	public abstract boolean isComplete() throws InvalidMessageException;
}
