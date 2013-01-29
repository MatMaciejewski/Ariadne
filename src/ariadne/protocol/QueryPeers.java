package ariadne.protocol;

import java.nio.ByteBuffer;

import ariadne.data.Catalogue;


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
	
	public static QueryPeers prepare(){
		QueryPeers q = new QueryPeers();
		ByteBuffer b = ByteBuffer.allocate(q.expectedLength());
		b.put(q.getCode());
		q.setByteBuffer(b);
		return q;
	}

	@Override
	public Response respond() {
		return ResponsePeers.prepare(Catalogue.getRandomPeers(PeerListResponse.MAX_PEERS));
	}
}
