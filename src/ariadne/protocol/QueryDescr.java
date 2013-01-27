package ariadne.protocol;

import ariadne.data.Hash;
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
}
