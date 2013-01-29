package ariadne.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import ariadne.data.Hash;
import ariadne.protocol.InvalidMessageException;
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

	public ResponseChase sendChaseQuery(Address addr, Hash hash, int timeout) {
		QueryChase q = QueryChase.prepare(getAddress().getPort(), hash);
		return (ResponseChase) sendQuery(q, new ResponseChase(), addr, timeout);
	}

	public ResponseDescr sendDescrQuery(Address addr, Hash hash, int timeout) {
		QueryDescr q = QueryDescr.prepare(getAddress().getPort(), hash);
		return (ResponseDescr) sendQuery(q, new ResponseDescr(), addr, timeout);
	}

	public ResponseChunk sendChunkQuery(Address addr, Hash hash, int chunkId, int expectedLength, int timeout) {
		QueryChunk q = QueryChunk.prepare(getAddress().getPort(), hash, chunkId);		
		return (ResponseChunk) sendQuery(q, new ResponseChunk(expectedLength), addr, timeout);
	}

	public ResponseBmask sendBmaskQuery(Address addr, Hash hash, int expectedSize, int timeout) {
		QueryBmask q = QueryBmask.prepare(getAddress().getPort(), hash);
		return (ResponseBmask) sendQuery(q, new ResponseBmask(expectedSize), addr, timeout);
	}

	public ResponsePeers sendPeersQuery(Address addr, int timeout) {
		QueryPeers q = QueryPeers.prepare();
		return (ResponsePeers) sendQuery(q, new ResponsePeers(), addr, timeout);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private Response sendQuery(Query q, Response r, Address addr, int timeout) {
		if (timeout < 0)
			throw new IllegalArgumentException();
		long remaining = (timeout == 0) ? Integer.MAX_VALUE : timeout;
		long current = System.currentTimeMillis();
		// Timeouts ignored for now

		
		try {
			Socket c = new Socket();
			c.connect(new InetSocketAddress(addr.getIpAddress().getHostAddress(), addr.getPort().getPort()), timeout);
			
			OutputStream out = c.getOutputStream();
			InputStream in = c.getInputStream();
			
			ByteBuffer buf = q.getByteBuffer();
			byte[] b = new byte[buf.limit()];
			buf.get(b, 0, b.length);
			
			out.write(b);
			
			byte[] resp = new byte[1024];
			int len;
			while(!r.isComplete()){
				
				len = in.read(resp);
				
				if(len < 1){ 
					Log.notice("<0 length data received in client - returning null");
					return null;
				}
				r.addBytes(resp, len);
			}
			
			c.close();
			return r;
		} catch (IOException e) {
			Log.warning("IOException in client sendQuery() method");
			return null;
		} catch (InvalidMessageException e) {
			Log.warning("InvalidMessageException received.");
			return null;
		}
	}
}
