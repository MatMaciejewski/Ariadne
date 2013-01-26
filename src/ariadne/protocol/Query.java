package ariadne.protocol;

import java.nio.ByteBuffer;

import ariadne.data.Hash;
import ariadne.net.Port;

public abstract class Query extends Message {
	
	public Port getPort() {
		return new Port(getByteBuffer(),1);
	}

	public Hash getHash() {
		return new Hash(getByteBuffer(), 3);
	}
	
	public abstract byte getCode();
	
	@Override
	public boolean isComplete() throws InvalidMessageException {
		ByteBuffer b = getByteBuffer();

		if (b.get(0) != getCode())
			throw new InvalidMessageException();
		if (b.limit() > 19)
			throw new InvalidMessageException();

		return (b.limit() == 19);
	}
}
