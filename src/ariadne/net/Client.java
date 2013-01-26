package ariadne.net;

import ariadne.data.Hash;
import ariadne.protocol.Response;


public interface Client {
	public Response sendChaseQuery(Address addr, Hash hash, int timeout);
	public Response sendDescrQuery(Address addr, Hash hash, int timeout);
	public Response sendCountQuery(Address addr, Hash hash, int timeout);
	public Response sendChunkQuery(Address addr, Hash hash, int timeout);
	public Response sendPeersQuery(Address addr, Hash hash, int timeout);
}
