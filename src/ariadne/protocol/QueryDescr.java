package ariadne.protocol;

import ariadne.data.Catalogue;
import ariadne.data.Database;
import ariadne.data.File;
import ariadne.data.Hash;
import ariadne.net.Address;
import ariadne.net.Port;


public class QueryDescr extends PortHashQuery {

	@Override
	public byte getCode() {
		return 3;
	}
	
	public static QueryDescr prepare(Port port, Hash hash){
		QueryDescr q = new QueryDescr();
		PortHashQuery.prepare(q, port, hash);
		return q;
	}

	@Override
	public Response respond() {
		if(getAuthor() != null){
			Address a = new Address(getAuthor().getIpAddress(), this.getPort());
			Catalogue.addPeer(getHash(), a, Catalogue.DEF_TIMEOUT, false);
		}
		ResponseDescr r;
		
		File f = Database.getFile(getHash());
		if(f == null){
			r = ResponseDescr.prepare(null);
		}else{
			r = ResponseDescr.prepare(f.getDescriptor());
		}
		
		return r;
	}
}
