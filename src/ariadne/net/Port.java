package ariadne.net;

import java.nio.ByteBuffer;

public class Port {
	private byte l;
	private byte r;

	public Port(byte l, byte r) {
		this.l = l;
		this.r = r;
	}

	public Port(int port) {
		r = (byte) (port & 0xFF);
		l = (byte) ((port >> 8) & 0xFF);
	}

	public Port(ByteBuffer b, int offset) {
		l = b.get(offset);
		r = b.get(offset+1);
	}

	public int getPort() {
		return ByteBuffer.wrap(new byte[] { 0, 0, l, r}).getInt();
	}

	public byte[] getBytes() {
		return new byte[] {l, r};
	}
}
