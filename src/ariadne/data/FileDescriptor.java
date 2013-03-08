package ariadne.data;

import java.nio.ByteBuffer;
import java.util.List;

public class FileDescriptor extends Descriptor {
	private FileDescriptor() {
	}

	public static FileDescriptor fromByteBuffer(ByteBuffer b) {
		if (b.remaining() < 16 + Hash.LENGTH)
			throw new IllegalArgumentException();

		FileDescriptor d = new FileDescriptor();
		d.init(b, false);

		int s = d.getChunkSize();
		int c = d.getChunkCount();
		long l = d.getFileSize();
		
		if(s < 1 || c < 1 || l < 1)
			throw new IllegalArgumentException();
		if (c * s < l)
			throw new IllegalArgumentException();
		if ((c - 1) * s >= l)
			throw new IllegalArgumentException();
		if (d.size() != 16 + Hash.LENGTH * c)
			throw new IllegalArgumentException();

		d.init(b, true);
		return d;
	}
	
	public static FileDescriptor fromData(int chunkSize, long fileSize, List<Hash> hashes){
		int c = hashes.size();
		int s = chunkSize;
		long l = fileSize;
		
		if(s < 1 || c < 1 || l < 1)
			throw new IllegalArgumentException();
		if (c * s < l)
			throw new IllegalArgumentException();
		if ((c - 1) * s >= l)
			throw new IllegalArgumentException();
		
		ByteBuffer b = ByteBuffer.allocate(16 + Hash.LENGTH * c);
		b.putInt(s);
		b.putInt(c);
		b.putLong(l);
		for(Hash h: hashes){
			b.put(h.getBuffer());
		}
		FileDescriptor d = new FileDescriptor();
		d.init(b, false);
		return d;
	}

	public int getChunkCount() {
		return buf.getInt(4);
	}

	public int getChunkSize() {
		return buf.getInt(0);
	}

	public long getFileSize() {
		return buf.getLong(8);
	}

	public Hash getChunkHash(int i) {
		return Hash.fromByteBuffer(getBufferPart(16 + i * Hash.LENGTH,
				Hash.LENGTH));
	}
}
