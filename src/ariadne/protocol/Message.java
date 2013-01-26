package ariadne.protocol;

import java.nio.ByteBuffer;

public abstract class Message {
	private ByteBuffer buf;

	public void addByteBuffer(ByteBuffer b) {
		if (buf == null) {
			buf = ByteBuffer.allocate(64);
		}

		if (b.limit() > buf.remaining()) {
			int ncap = buf.capacity();
			while (ncap < buf.limit() + b.limit()) {
				ncap *= 2;
			}
			ByteBuffer nbuf = ByteBuffer.allocate(ncap);
			nbuf.put(buf);
			buf = nbuf;
		}
		buf.put(b);
	}
	
	public ByteBuffer getByteBuffer(){
		return buf.asReadOnlyBuffer();
	}
	
	public abstract boolean isComplete() throws InvalidMessageException;
}
