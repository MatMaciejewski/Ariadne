package ariadne.protocol;

import java.nio.ByteBuffer;

import ariadne.data.Hash;
import ariadne.net.Port;

/*
 * CHASE query
 * 
 * Byte 0		- query code
 * Byte 1-2		- client port (server is gonna save it along with our IP)
 * Byte 3-18	- chased hash 
 */

public abstract class PortHashQuery extends Query {
	public Port getPort(){
		return new Port(getByteBuffer(), 1);
	}
	public Hash getHash(){
		return new Hash(getByteBuffer(), 3);
	}
	@Override
	public int expectedLength(){
		return 19;
	}
}
