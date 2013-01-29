package ariadne.protocol;

import java.util.List;

import ariadne.Application;
import ariadne.data.Catalogue;
import ariadne.data.Hash;
import ariadne.net.Address;
import ariadne.net.Port;


public class QueryChase extends PortHashQuery {
	
	@Override
	public byte getCode() {
		return 1;
	}
	
	public static QueryChase prepare(Port port, Hash hash){
		QueryChase q = new QueryChase();
		PortHashQuery.prepare(q, port, hash);
		return q;
	}

	@Override
	public Response respond() {
		List<Address> addresses = Catalogue.getPeerForHash(getHash(), PeerListResponse.MAX_PEERS);
		
		//do we chase this file?
		boolean interested = false;
		if(Application.getManager().getTasks().contains(getHash())){
			interested = true;
		}
		return ResponseChase.prepare(addresses, interested);
	}
}
