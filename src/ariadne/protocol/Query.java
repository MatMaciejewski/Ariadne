package ariadne.protocol;

import java.nio.ByteBuffer;

public abstract class Query extends Message {
	public abstract byte getCode();
	public abstract int expectedLength();
	@Override
	public boolean isComplete() throws InvalidMessageException {
		ByteBuffer b = getByteBuffer();
		if(b.limit() == 0) return false;
		if(b.get(0) != getCode()) throw new InvalidMessageException();
		if(b.limit() > expectedLength()) throw new InvalidMessageException();
		return (b.limit() == expectedLength());
	}
	
	public abstract Response respond();
}
