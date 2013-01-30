package ariadne.data;

import java.nio.ByteBuffer;
import java.util.HashSet;

import ariadne.utils.DiskResource;

/*
 * Byte 0-3		- Chunk count
 * Byte 4-7		- Chunk size in bytes
 * Byte 8-15	- Total file size
 * Byte 16-*	- Chunk hashes
 */

public class Descriptor {
	public final static int SMALLEST_ALLOWED_CHUNK_SIZE = Hash.LENGTH;
	private ByteBuffer data;
	
	public Descriptor(int chunkSize, long fileSize, HashSet<Hash> hashes) {
		if ((chunkSize < SMALLEST_ALLOWED_CHUNK_SIZE) || (hashes == null) || (hashes.size() == 0))
			throw new IllegalArgumentException();
		if(chunkSize * hashes.size() > fileSize) 
			throw new IllegalArgumentException();

		data = ByteBuffer.allocate(4 + 4 + 8 + Hash.LENGTH * hashes.size());

		data.rewind();
		data.putInt(hashes.size());
		data.putInt(chunkSize);
		data.putLong(fileSize);
		for (Hash h : hashes) {
			data.put(h.getByteBuffer());
		}
	}

	public BitMask getEmptyBitmask(){
		return new BitMask(getChunkCount());
	}
	
	public Hash getHash() {
		return Hash.computeFromByteBuffer(data);
	}

	public Hash getChunkHash(int id) {
		if (id < 0 || id >= getChunkCount())
			throw new IllegalArgumentException();
		return new Hash(data, 4 + 4 + 8 + Hash.LENGTH * id);
	}

	public int getChunkSize() {
		data.position(4);
		return data.getInt();
	}

	public int getChunkCount() {
		data.position(0);
		return data.getInt();
	}
	
	public long getFileSize(){
		data.position(8);
		return data.getLong();
	}

	public ByteBuffer getByteBuffer() {
		ByteBuffer b = data.asReadOnlyBuffer();
		b.rewind();
		return b;
	}

	private Descriptor() {
	}

	public static Descriptor parse(ByteBuffer b, int offset) {
		if(b == null) return null;
		try {
			b.position(offset);
			int chunkCount = b.getInt();
			int chunkSize = b.getInt();
			long fileSize = b.getLong();
			if ((chunkSize < SMALLEST_ALLOWED_CHUNK_SIZE) || (chunkCount <= 0) || (b.remaining() < Hash.LENGTH * chunkCount))
				throw new IllegalArgumentException();
			Descriptor d = new Descriptor();
			d.data = ByteBuffer.allocate(4 + 4 + 8 + Hash.LENGTH * chunkCount);
			d.data.putInt(chunkCount);
			d.data.putInt(chunkSize);
			d.data.putLong(fileSize);
			b.position(offset + 4 + 4 + 8);
			for (int i = Hash.LENGTH * chunkCount; i > 0; --i) {
				d.data.put(b.get());
			}

			return d;
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException();
		}
	}

	public static Descriptor fromFile(String fileName){
		Descriptor d = null;
		DiskResource r = DiskResource.open(fileName, false);
		if(r != null){
			ByteBuffer b = r.readAll();
			if(b != null){
				d = Descriptor.parse(b, 0);
			}
			r.close();
		}
		return d;
	}
	
	public boolean saveToFile(String fileName){
		return DiskResource.putFileContents(getByteBuffer(), fileName);
	}
	
	public static String getDefaultFileName(String path, String fileName){
		return path + "/." + fileName + ".desc";
	}

}
