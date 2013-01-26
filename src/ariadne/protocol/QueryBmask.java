package ariadne.protocol;

import java.nio.ByteBuffer;


/*
 * CHASE Bmask
 * 
 * Byte 0		- query code
 * Byte 1-2		- client port (server is gonna save it along with our IP)
 * Byte 3-18	- chased file
 */

public class QueryBmask extends Query {

	@Override
	public byte getCode() {
		return 3;
	}

	@Override
	public boolean isComplete() throws InvalidMessageException {
		ByteBuffer b = getByteBuffer();
		
		if(b.get(0) != getCode()) 
			throw new InvalidMessageException();
		if(b.limit() > 19) 
			throw new InvalidMessageException();
		
		return (b.limit() == 19);
	}



}
