package ariadne.data;

import java.nio.ByteBuffer;
import java.util.List;

/*
 * Byte 0-3		- Chunk count
 * Byte 4-7		- Chunk size in bytes
 * Byte 8-*		- Chunk hashes
 */

public class Descriptor {
	public final static int SMALLEST_ALLOWED_CHUNK_SIZE = Hash.LENGTH;
	private ByteBuffer data;

	public Descriptor(int chunkSize, List<Hash> hashes) {
		if ((chunkSize < SMALLEST_ALLOWED_CHUNK_SIZE) || (hashes == null) || (hashes.size() == 0))
			throw new IllegalArgumentException();

		data = ByteBuffer.allocate(8 + Hash.LENGTH * hashes.size());

		data.rewind();
		data.putInt(hashes.size());
		data.putInt(chunkSize);
		for (Hash h : hashes) {
			data.put(h.getByteBuffer());
		}
	}

	public Hash getHash() {
		return Hash.computeFromByteBuffer(data);
	}

	public Hash getChunkHash(int id) {
		if (id < 0 || id >= getChunkCount())
			throw new IllegalArgumentException();
		return new Hash(data, 8 + Hash.LENGTH * id);
	}

	public int getChunkSize() {
		data.position(4);
		return data.getInt();
	}

	public int getChunkCount() {
		data.position(0);
		return data.getInt();
	}

	public ByteBuffer getByteBuffer() {
		return data.asReadOnlyBuffer();
	}
	
	private Descriptor(){}

	public static Descriptor parse(ByteBuffer b, int offset) {
		try {
			b.position(offset);
			int chunkCount = b.getInt();
			int chunkSize = b.getInt();

			if ((chunkSize < SMALLEST_ALLOWED_CHUNK_SIZE) || (chunkCount <= 0) || (b.remaining() <= Hash.LENGTH * chunkCount))
				throw new IllegalArgumentException();
			
			Descriptor d = new Descriptor();
			d.data = ByteBuffer.allocate(8 + Hash.LENGTH * chunkCount);
			d.data.putInt(chunkCount);
			d.data.putInt(chunkSize);
			b.position(offset);
			for(int i = d.data.capacity(); i>0; --i){
				d.data.put(b.get());
			}

			return d;
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException();
		}
	}
}
