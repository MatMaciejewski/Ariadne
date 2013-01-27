package ariadne.protocol;

/*
 * CHUNK query
 * 
 * Byte 0		- query code
 * Byte 1-2		- client port (server is gonna save it along with our IP)
 * Byte 3-18	- chased hash 
 * Byte 19-22	- chunk id
 */

public class QueryChunk extends PortHashQuery {

	public int getChunkId(){
		getByteBuffer().position(19);
		return getByteBuffer().getInt();
	}

	@Override
	public byte getCode() {
		return 2;
	}
	
	@Override
	public int expectedLength(){
		return 23;
	}
}
