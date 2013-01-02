package ariadne.core.protocol;

import java.util.ArrayList;
import java.util.List;

public abstract class Message {
	public enum Type{
		CHASE_MESSAGE ((byte)0, true, ChaseMessage.class),
		COUNT_MESSAGE ((byte)1, true, null),
		CHUNK_MESSAGE ((byte)2, true, null),
		DESCR_MESSAGE ((byte)3, true, null),
		
		CHASE_RESPONSE (null, false, ChaseResponse.class);
		
		public Byte code;
		private boolean isQuery;
		public Class<? extends Message> className;
		
		Type(Byte v, boolean isQuery, Class<? extends Message> c){
			code = v;
			className = c;
			this.isQuery = isQuery;
		}
		
		public boolean isQuery(){
			return isQuery;
		}
		
		public boolean isResponse(){
			return !isQuery;
		}
	}
	public static class InvalidMessageException extends Exception{
		private static final long serialVersionUID = 1L;
		
	}
	///////////////////
	
	protected List<Byte> data;
	
	public Message(){
		data = new ArrayList<Byte>();
	}
	public void addData(byte[] d, int limit){
		for(int i=0;i<limit;++i){
			data.add(d[i]);
		}
	}
	
	public abstract Type getType();
	public abstract boolean isComplete() throws InvalidMessageException;
	public byte[] getBytes(){
		byte[] raw = new byte[data.size()];
		for(int i=0;i<data.size();++i){
			raw[i] = data.get(i);
		}
		return raw;
	}
}

