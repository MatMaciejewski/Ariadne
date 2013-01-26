package ariadne.net;

import ariadne.data.Hash;
import ariadne.protocol.Response;

/*
 * Code chart:
 * 0 - ChaseQuery
 * 1 - DescrQuery
 * 2 - Chunk Query
 * 3 - Bmask Query
 * 4 - Peers Query
 */
public interface Client {
	public Response sendChaseQuery(Address addr, Hash hash, int timeout);
	public Response sendDescrQuery(Address addr, Hash hash, int timeout);
	public Response sendChunkQuery(Address addr, Hash hash, int timeout);
	public Response sendBmaskQuery(Address addr, Hash hash, int timeout);
	public Response sendPeersQuery(Address addr, Hash hash, int timeout);
}
