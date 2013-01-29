package ariadne.protocol;

import java.util.List;

import ariadne.net.Address;

public class ResponseChase extends PeerListResponse{
	
	public static ResponseChase prepare(List<Address> peers, boolean iAmInterested){
		ResponseChase r = new ResponseChase();
		byte b = (byte) (peers.size() | 0x80);
		PeerListResponse.prepare(r, peers, b);
		return r;
	}
	
	public boolean isInterested(){
		return (getByteBuffer().get(0) & 0x80) != 0;
	}
	
	@Override
	public int getPeerCount() {
		byte c = getByteBuffer().get(0);
		return (c & 0x7F);
	}
}
