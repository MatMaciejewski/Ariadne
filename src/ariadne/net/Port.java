package ariadne.net;

import java.nio.ByteBuffer;

public class Port {
	public final static int BYTESIZE = 2;
	private ByteBuffer data;

	public Port(byte l, byte r) {
		data = ByteBuffer.allocate(BYTESIZE);
		data.put(l).put(r);
	}

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
		return ByteBuffer.allocate(4).put((byte) 0).put((byte) 0).put(data).getInt(0);
	}
	
	public ByteBuffer getByteBuffer(){
		return data.asReadOnlyBuffer();
	}
}
