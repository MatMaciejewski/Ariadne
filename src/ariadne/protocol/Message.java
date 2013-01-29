package ariadne.protocol;

import java.nio.ByteBuffer;

public abstract class Message {
	private ByteBuffer buf;
	private int buflen;
	
	@Deprecated
	public void addBytes(byte[] b, int length){
		ByteBuffer bb = ByteBuffer.allocate(length);
		bb.put(b, 0, length);
		bb.flip();
		
		addByteBuffer(bb);
	}
	
	public void addByteBuffer(ByteBuffer b){
		//we expect that b has position = 0, limit = size and c>=limit
		//also, buf has position=pos, limit = size and c>= limit
		
		if (buf == null) {
			buf = ByteBuffer.allocate(64);
			buf.position(0);
			buf.flip();
		}
		
		System.out.println("Our buffer has P="+buf.position()+" L="+buf.limit()+" C:"+buf.capacity());
		System.out.println("We read from buffer with P="+b.position()+" L="+b.limit()+" C:"+b.capacity());
		
		if( buf.capacity() < b.limit() + buf.limit() ){
			int s = buf.capacity();
			while(s < b.limit() + buf.limit()){
				s *= 2;
			}
			ByteBuffer nbuf = ByteBuffer.allocate(s);
			nbuf.position(0);
			nbuf.limit(0);
			buf.flip();
			nbuf.put(buf);
			buf = nbuf;
			System.out.println("Reallocated! Now it is P="+buf.position()+" L="+buf.limit()+" C:"+buf.capacity());
		}
		
		b.position(0);
		buf.put(b);
		System.out.println("Finally our buffer has P="+buf.position()+" L="+buf.limit()+" C:"+buf.capacity());
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
