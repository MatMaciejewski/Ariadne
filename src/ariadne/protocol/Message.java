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
	
	protected void setByteBuffer(ByteBuffer b){
		if(b.isReadOnly()) throw new IllegalArgumentException();
		buf = b;
	}
	
	protected ByteBuffer accessByteBuffer(){
		return buf;
	}
	
	public abstract boolean isComplete() throws InvalidMessageException;
}
