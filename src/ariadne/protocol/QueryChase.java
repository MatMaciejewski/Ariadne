package ariadne.protocol;


import java.util.LinkedList;
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
		ResponseChase r;
		
		List<Address> addresses = Catalogue.getPeerForHash(getHash());
		
		//do we chase this file?
		if(Application.getManager().getTasks().contains(getHash())){
			addresses.add(Application.getClient().getAddress());
		}
		return ResponseChase.prepare(addresses);
	}
}
