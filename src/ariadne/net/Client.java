package ariadne.net;

import ariadne.data.Hash;
import ariadne.protocol.Query;
import ariadne.protocol.QueryBmask;
import ariadne.protocol.QueryChase;
import ariadne.protocol.QueryChunk;
import ariadne.protocol.QueryDescr;
import ariadne.protocol.QueryPeers;
import ariadne.protocol.Response;
import ariadne.protocol.ResponseBmask;
import ariadne.protocol.ResponseChase;
import ariadne.protocol.ResponseChunk;
import ariadne.protocol.ResponseDescr;
import ariadne.protocol.ResponsePeers;

/*
 * Code chart:
 * 0 - ChaseQuery
 * 1 - DescrQuery
 * 2 - Chunk Query
 * 3 - Bmask Query
 * 4 - Peers Query
 */
public class Client {
	private Address clientAddress;

	public Client(Address address) {
		clientAddress = address;
	}

	public Address getAddress() {
		return clientAddress;
	}

	public Response sendChaseQuery(Address addr, Hash hash, int timeout) {
		QueryChase q = QueryChase.prepare(getAddress().getPort(), hash);
		return sendQuery(q, new ResponseChase(), addr, timeout);
	}

	public Response sendDescrQuery(Address addr, Hash hash, int timeout) {
		QueryDescr q = QueryDescr.prepare(getAddress().getPort(), hash);
		return sendQuery(q, new ResponseDescr(), addr, timeout);
	}

	public Response sendChunkQuery(Address addr, Hash hash, int chunkId, int expectedLength, int timeout) {
		QueryChunk q = QueryChunk.prepare(getAddress().getPort(), hash, chunkId);
		return sendQuery(q, new ResponseChunk(expectedLength), addr, timeout);
	}

	public Response sendBmaskQuery(Address addr, Hash hash, int timeout) {
		QueryBmask q = QueryBmask.prepare(getAddress().getPort(), hash);
		return sendQuery(q, new ResponseBmask(), addr, timeout);
	}

	public Response sendPeersQuery(Address addr, int timeout) {
		QueryPeers q = QueryPeers.prepare();
		return sendQuery(q, new ResponsePeers(), addr, timeout);
	}
	
	private Response sendQuery(Query q, Response r, Address addr, int timeout){
		return null;
	}
}
