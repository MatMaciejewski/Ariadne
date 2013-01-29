package ariadne.protocol;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ariadne.net.Address;

/*
 * Byte 0 			- peer count
 * Bytes 1,7,13...	- peer ip+port
 */

abstract class PeerListResponse extends Response {
	public static final int MAX_PEERS = 32;

	public List<Address> getPeers() {
		ByteBuffer b = getByteBuffer();
		int s = getPeerCount();
		List<Address> result = new LinkedList<Address>();

		for (int i = 0; i < s; ++i) {
			result.add(Address.fromByteBuffer(b, 1+i*Address.BYTESIZE));
		}
		return result;
	}

	public int getPeerCount() {
		return getByteBuffer().get(0);
	}

	@Override
	public boolean isComplete() throws InvalidMessageException {
		ByteBuffer b = getByteBuffer();
		if (b.limit() == 0)
			return false;
		if (getPeerCount() > MAX_PEERS)
			throw new InvalidMessageException();
		int s = 1 + Address.BYTESIZE * getPeerCount();
		if (b.limit() > s)
			throw new InvalidMessageException();
		return (b.limit() == s);
	}
	
	protected static void prepare(PeerListResponse r, List<Address> peers, byte firstByte){
		if(peers.size() > MAX_PEERS) throw new IllegalArgumentException();
		ByteBuffer b = ByteBuffer.allocate(1 + peers.size() * Address.BYTESIZE);
		b.put(firstByte);
		for(Address a: peers){
			b.put(a.getByteBuffer());
		}
		r.setByteBuffer(b);
	}	

}
