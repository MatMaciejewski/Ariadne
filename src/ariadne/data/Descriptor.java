package ariadne.data;

import java.nio.ByteBuffer;

public class Descriptor extends ByteSource {
	private Descriptor() {
	}
	
	public static Descriptor fromByteBuffer(ByteBuffer b){
		if(b.remaining() < 16+Hash.LENGTH)
			throw new IllegalArgumentException();
		
		Descriptor d = new Descriptor();
		d.init(b, false);
		
		int c = d.getChunkCount();
		int s = d.getChunkSize();
		long l = d.getFileSize();
		
		if(c*s < l) throw new IllegalArgumentException();
		if((c-1)*s >= l) throw new IllegalArgumentException();
		if(d.size() != 16+Hash.LENGTH*c) throw new IllegalArgumentException();
		
		d.init(b, true);
		return d;
	}

	public int getChunkCount() {
		return buf.getInt(0);
	}

	public int getChunkSize() {
		return buf.getInt(4);
	}

	public long getFileSize() {
		return buf.getLong(8);
	}

	public Hash getChunkHash(int i) {
		return Hash.fromByteBuffer(
				getBufferPart(16 + i * Hash.LENGTH,Hash.LENGTH));
	}
}
