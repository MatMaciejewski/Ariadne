package ariadne.data;

import java.nio.ByteBuffer;

public class Chunk extends ByteSource{
	private Chunk(){}
	
	public static Chunk fromByteBuffer(ByteBuffer src){
		Chunk c = new Chunk();
		c.init(src, true);
		return c;
	}
}
