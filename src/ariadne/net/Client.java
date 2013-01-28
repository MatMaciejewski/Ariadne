package ariadne.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

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
import ariadne.utils.Log;

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

	public Response sendChunkQuery(Address addr, Hash hash, int chunkId,
			int expectedLength, int timeout) {
		QueryChunk q = QueryChunk
				.prepare(getAddress().getPort(), hash, chunkId);
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
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private Response sendQuery(Query q, Response r, Address addr, int timeout) {
		try {
			SocketChannel sock = SocketChannel.open();

			// non blocking
			sock.configureBlocking(false);

			// connect to a running server
			sock.connect(new InetSocketAddress(addr.getIpAddress(), addr.getPort().getPort()));

			// get a selector
			Selector selector = Selector.open();

			// register the client socket with "connect operation" to the
			// selector
			sock.register(selector, SelectionKey.OP_CONNECT);

			// select() blocks until something happens on the underlying socket
			
			

			return null;
		} catch (Exception e) {
			Log.notice("Exception in client code.");
			return null;
		}

	}
}
