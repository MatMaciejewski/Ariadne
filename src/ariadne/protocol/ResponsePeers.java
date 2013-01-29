package ariadne.protocol;

import java.util.List;

import ariadne.net.Address;

public class ResponsePeers extends PeerListResponse{
	
	
	public static ResponsePeers prepare(List<Address> peers){
		ResponsePeers r = new ResponsePeers();
		PeerListResponse.prepare(r, peers, (byte) peers.size());
		return r;
	}
}
