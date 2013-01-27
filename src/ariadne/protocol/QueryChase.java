package ariadne.protocol;

/*
 * CHASE query
 * 
 * Byte 0		- query code
 * Byte 1-2		- client port (server is gonna save it along with our IP)
 * Byte 3-18	- chased hash 
 * 
 */

public class QueryChase extends PortHashQuery {
	
	@Override
	public byte getCode() {
		return 1;
	}
}
