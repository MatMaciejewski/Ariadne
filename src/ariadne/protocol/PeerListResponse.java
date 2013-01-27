package ariadne.protocol;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ariadne.net.Address;

/*
 * Byte 0 			- peer count
 * Bytes 1,7,13...	- peer ip+port
 */

public class PeerListResponse extends Response {
	public static final int MAX_PEERS = 32;

	public List<Address> getPeers() {
		ByteBuffer b = getByteBuffer();
		int s = getPeerCount();
		List<Address> result = new LinkedList<Address>();

		for (int i = 0; i < s; ++i) {
			result.add(Address.fromByteBuffer(b, 1+i*6));
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
		if (b.get(0) > MAX_PEERS)
			throw new InvalidMessageException();
		int s = 1 + 6 * b.get(0);
		if (b.limit() > s)
			throw new InvalidMessageException();
		return (b.limit() == s);
	}

}
