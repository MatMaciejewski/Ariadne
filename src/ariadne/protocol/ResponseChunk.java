package ariadne.protocol;

import java.nio.ByteBuffer;

import ariadne.data.Chunk;

/*
 * Byte 0		- 0 if not found, else found
 * Bytes 1-*	- chunk
 */

public class ResponseChunk extends Response {
	private int chunkLength;
	private boolean success;

	public ResponseChunk(int chunkLength) {
		success = false;
		if (chunkLength < 0)
			throw new IllegalArgumentException();
		this.chunkLength = chunkLength;
	}

	public boolean chunkFound() {
		return success;
	}
	
	public Chunk getChunk(){
		return (success) ? new Chunk(getByteBuffer(), 1, chunkLength) : null;
	}

	@Override
	public boolean isComplete() throws InvalidMessageException {
		ByteBuffer b = getByteBuffer();
		if (b.limit() == 0)
			return false;
		else{
			if (b.get(0) == 0) {
				if (b.limit() == 1) {
					return true;
				} else {
					throw new InvalidMessageException();
				}
			} else {
				success = (b.limit() == 1+chunkLength);
				return success;
			}
		}
	}
	
	public static ResponseChunk prepare(Chunk c){
		ResponseChunk r;
		
		if(c == null){
			r = new ResponseChunk(0);
		}else{
			r = new ResponseChunk(c.getSize());
			ByteBuffer b = ByteBuffer.allocate(1+c.getSize());
			b.put((byte) 1);
			b.put(c.getByteBuffer());
			r.setByteBuffer(b);
		}
		
		return r;
	}
}
