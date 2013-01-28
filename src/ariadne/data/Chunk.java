package ariadne.data;

import java.nio.ByteBuffer;

public class Chunk {
	private ByteBuffer data;
	
	public Chunk(ByteBuffer b){
		this(b, 0, b.capacity());
	}
	
	public Chunk(byte[] b){
		this(b, 0, b.length);
	}
	
	public Chunk(ByteBuffer b, int offset, int length){
		b.position(offset);
		data = ByteBuffer.allocate(length);
		for(int i=0;i<length;++i) data.put(b.get());
	}
	
	public Chunk(byte[] b, int offset, int length){
		data = ByteBuffer.allocate(length).put(b, offset, length);
	}
	
	public Hash getHash(){
		return Hash.computeFromByteBuffer(data);
	}
	
	public int getSize(){
		return data.capacity();
	}
	
	public ByteBuffer getByteBuffer(){
		ByteBuffer b = data.asReadOnlyBuffer();
		b.rewind();
		return b;
	}
}
