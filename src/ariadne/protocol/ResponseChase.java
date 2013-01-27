package ariadne.protocol;

import java.util.List;

import ariadne.net.Address;

public class ResponseChase extends PeerListResponse{
	
	public static ResponseChase prepare(List<Address> peers){
		ResponseChase r = new ResponseChase();
		PeerListResponse.prepare(r, peers);
		return r;
	}
}
