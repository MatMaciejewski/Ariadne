package ariadne.net;

import ariadne.data.Hash;
import ariadne.protocol.PeerListResponse;
import ariadne.protocol.Response;
import ariadne.protocol.ResponseBmask;
import ariadne.protocol.ResponseChunk;
import ariadne.protocol.ResponseDescr;

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
	
	public Client(Address address){
		clientAddress=address;
	}
	
	public Address getAddress(){
		return clientAddress;
	}
	
	public Response sendChaseQuery(Address addr, Hash hash, int timeout){
		return new PeerListResponse();
	}
	public Response sendDescrQuery(Address addr, Hash hash, int timeout){
		return new ResponseDescr();
	}
	public Response sendChunkQuery(Address addr, Hash hash, int chunkId, int timeout){
		return new ResponseChunk();
	}
	public Response sendBmaskQuery(Address addr, Hash hash, int timeout){
		return new ResponseBmask();
	}
	public Response sendPeersQuery(Address addr, Hash hash, int timeout){
		return new PeerListResponse();
	}
}
