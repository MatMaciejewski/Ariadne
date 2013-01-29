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
		
		if( buf.capacity() < b.limit() + buf.limit() ){
			int s = buf.capacity();
			while(s < b.limit() + buf.limit()){
				s *= 2;
			}
			ByteBuffer nbuf = ByteBuffer.allocate(s);
			nbuf.position(0);
			buf.position(0);
			nbuf.put(buf);
			nbuf.limit(buf.limit());
			buf = nbuf;
		}
		
		b.position(0);
		buf.limit(buf.limit() + b.limit());
		buf.put(b);
	}

	
	public ByteBuffer getByteBuffer(){
		ByteBuffer b;
		if(buf == null){
			b = ByteBuffer.allocate(1);
			b.position(0);
			b.limit(0);
		} else {
			b = buf.asReadOnlyBuffer();
			b.rewind();
		}
		return b;
	}
	
	public void print(){
		ByteBuffer b = getByteBuffer();
		System.out.println("MESSAGE " + this);
		for(int i=0;i<b.limit();++i){
			System.out.print((byte) b.get(i) + " ");
		}
		System.out.println("END");
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
