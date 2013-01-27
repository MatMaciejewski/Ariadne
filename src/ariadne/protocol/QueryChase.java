package ariadne.protocol;


import ariadne.data.Hash;
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
}
