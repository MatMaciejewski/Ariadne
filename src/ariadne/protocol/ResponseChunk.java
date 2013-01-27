package ariadne.protocol;

import java.nio.ByteBuffer;

import ariadne.data.Chunk;

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
		return new Chunk(getByteBuffer(), 1, chunkLength);
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
				return (b.limit() == 1+chunkLength);
			}
		}
	}
}
