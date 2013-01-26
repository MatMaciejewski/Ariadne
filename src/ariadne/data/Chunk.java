package ariadne.data;

public class Chunk {
	private Hash hash;
	private byte[] content;

	public Chunk(byte[] source) {
		content = source;
		hash = Hash.computeFromBytes(source);
	}

	public Hash getHash() {
		return hash;
	}

	public int getSize() {
		return content.length;
	}

	public byte[] getBytes() {
		return content;
	}
}
