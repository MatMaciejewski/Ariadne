package ariadne.protocol;

import java.nio.ByteBuffer;


/*
 * PEERS query
 * 
 * Byte 0		- query code
 * Byte 1-2		- client port (server is gonna save it along with our IP)
 *  
 */

public class QueryPeers extends Query {

	@Override
	public byte getCode() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public boolean isComplete() throws InvalidMessageException {
		ByteBuffer b = getByteBuffer();

		if (b.get(0) != getCode())
			throw new InvalidMessageException();
		if (b.limit() > 3)
			throw new InvalidMessageException();

		return (b.limit() == 3);
	}

}
