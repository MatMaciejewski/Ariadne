package ariadne.protocol;

import ariadne.data.Database;
import ariadne.data.File;
import ariadne.data.Hash;
import ariadne.net.Port;


public class QueryBmask extends PortHashQuery {

	@Override
	public byte getCode() {
		return 4;
	}
	
	public static QueryBmask prepare(Port port, Hash hash){
		QueryBmask q = new QueryBmask();
		PortHashQuery.prepare(q, port, hash);
		return q;
	}

	@Override
	public Response respond() {
		ResponseBmask r;
		
		File f = Database.getFile(getHash());
		if(f == null){
			r = ResponseBmask.prepare(null);
		}else{
			r = ResponseBmask.prepare(f.getBitMask());
		}
		return r;
	}
}
