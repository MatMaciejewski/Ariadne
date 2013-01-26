package ariadne.protocol;

import java.nio.ByteBuffer;
import ariadne.data.Hash;
import ariadne.net.Port;

/*
 * DESCR query
 * 
 * Byte 0		- query code
 * Byte 1-2		- client port (server is gonna save it along with our IP)
 * Byte 3-18	- chased hash 
 */

public class QueryDescr extends Query {

	@Override
	public byte getCode() {
		return 1;
	}

	public Port getPort() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public Hash getHash() {
		return new Hash(getByteBuffer(), 3);
	}

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
