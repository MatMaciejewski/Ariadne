package ariadne.protocol;

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
}
