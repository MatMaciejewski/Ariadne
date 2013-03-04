package ariadne.data;

import java.nio.ByteBuffer;

/**
 * @author eipifi
 * Interface for all classes capable of returning a bytebuffer
 */
public abstract class ByteSource {
	protected ByteBuffer buf;
	
	protected void init(ByteBuffer src, boolean copy){
		if(copy){
			buf = ByteBuffer.allocate(src.remaining()).put(src);
		}else{
			buf = src;
		}
	}
	
	protected void init(byte[] src, boolean copy){
		if(copy){
			buf = ByteBuffer.wrap(src);
		}else{
			buf = ByteBuffer.allocate(src.length);
			buf.put(src);
		}
	}
	
	public ByteBuffer getBuffer(){
		ByteBuffer b = buf.asReadOnlyBuffer();
		b.clear();
		return b;
	}
	
	public ByteBuffer getBufferPart(int offset, int length){
		ByteBuffer b = getBuffer();
		b.position(offset).limit(offset+length);
		b = b.slice();
		b.clear();
		return b;
	}
	
	public int size(){
		return buf.capacity();
	}
	
	@Override
	public int hashCode(){
		return buf.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(o.getClass() == getClass()){
			ByteSource b = (ByteSource) o;
			return getBuffer().equals(b.getBuffer());
		}
		return false;
	}
}
