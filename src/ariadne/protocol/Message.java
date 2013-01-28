package ariadne.protocol;

import java.nio.ByteBuffer;

public abstract class Message {
	private ByteBuffer buf;
	private int buflen;
	
	@Deprecated
	public void addBytes(byte[] b, int length){
		System.out.println("len:" + length);
		ByteBuffer bb = ByteBuffer.allocate(length);
		bb.put(b, 0, length);
		addByteBuffer(bb);
	}

	public void addByteBuffer(ByteBuffer b) {
		if (buf == null) {
			buflen = 0;
			buf = ByteBuffer.allocate(64);
		}
		buf.position(buflen);
		

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
		buf.flip();
		buflen = b.position();
	}
	
	public ByteBuffer getByteBuffer(){
		if(buf == null){
			return ByteBuffer.allocate(1);
		} else {
			ByteBuffer b = buf.asReadOnlyBuffer();
			b.rewind();
			return b;
		}
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
