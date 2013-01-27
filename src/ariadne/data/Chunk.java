package ariadne.data;

import java.nio.ByteBuffer;

public class Chunk {
	private byte[] content;

	public Chunk(byte[] source) {
		content = source;
	}
	
	public Chunk(ByteBuffer b, int offset, int length){
		content = new byte[length];
		b.position(offset);
		b.get(content);
	}

	public Hash getHash() {
		return Hash.computeFromBytes(content);
	}

	public int getSize() {
		return content.length;
	}

	public byte[] getBytes() {
		return content;
	}
}
