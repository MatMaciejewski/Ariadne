package ariadne.net;

import java.nio.ByteBuffer;

public class Port {
	public final static int BYTESIZE = 2;
	private ByteBuffer data;

	public Port(int port) {
		data = ByteBuffer.allocate(BYTESIZE);
		data.put((byte) (port & 0xFF)).put((byte) ((port >> 8) & 0xFF));
		
	}

	public Port(ByteBuffer b, int offset) {
		data = ByteBuffer.allocate(BYTESIZE);
		b.position(offset);
		data.put(b.get()).put(b.get());
	}

	public int getPort() {
		return ((data.get(1) & 0xFF) << 8) + (data.get(0) & 0xFF);
	}
	
	public ByteBuffer getByteBuffer(){
		ByteBuffer b = data.asReadOnlyBuffer();
		b.rewind();
		return b;
	}
}
