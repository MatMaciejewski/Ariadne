package ariadne.protocol;

import java.nio.ByteBuffer;

public abstract class Message {
	private ByteBuffer buf;
	
	public void addBytes(ByteBuffer b){
		if(buf == null){
			buf = ByteBuffer.allocate(64);
		}
		
		if(b.limit() > buf.remaining()){
			//ByteBuffer nbuf = ByteBuffer.allocate();
		}
		
	}
}
