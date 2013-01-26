package ariadne.protocol;


/*
 * PEERS query
 * 
 * Byte 0		- query code
 */

public class QueryPeers extends Query {

	@Override
	public byte getCode() {
		return 0;
	}

	@Override
	public int expectedLength() {
		return 1;
	}
}
