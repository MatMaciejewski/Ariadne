package ariadne.data;

import java.nio.ByteBuffer;

import ariadne.utils.DiskResource;

public class BitMask {
	private ByteBuffer mask;
	private int size;
	private int posessed;
	
	/**
	 * Create new bitmask, filled with 0
	 * 
	 * @param size
	 *            chunkCount
	 */
	public BitMask(int size) {
		if (size < 0)
			throw new IllegalArgumentException();

		this.size = size;
		int s = bytesRequiredForSize(size);
		mask = ByteBuffer.allocate(s);

		for (int i = 0; i < mask.capacity(); ++i) {
			mask.put((byte) 0);
		}
		posessed = 0;
	}
	
	private BitMask(){}
	
	public static BitMask parse(ByteBuffer b, int chunkCount){
		if((b == null) || (chunkCount < 1)) return null;
		
		int bytesRequired = bytesRequiredForSize(chunkCount);
		if(b.remaining() < bytesRequired) return null;
		
		int pos = b.position();
		
		BitMask bm = new BitMask();
		bm.size = chunkCount;
		bm.posessed = 0;
		bm.mask = ByteBuffer.allocate(bytesRequired);
		
		for(int i=0;i<bytesRequired;++i){
			bm.mask.put( b.get() );
		}
		for(int i=0;i<bm.getSize();++i){
			if(bm.isSet(i)) bm.posessed++;
		}
		
		b.position(pos);
		return bm;
	}
	
	public BitMask getDiff(BitMask peer) {
		BitMask difference = new BitMask(getSize());
		if (getSize() != peer.getSize())
			throw new IllegalArgumentException();
		for (int i = 0; i < getSize(); i++)
			if (!isSet(i) && peer.isSet(i))
				difference.set(i);
		return difference;
	}

	/**
	 * Compare to an empty bitmask
	 * 
	 * @return true if it's all 0
	 */
	public boolean compareToNull() {
		return (getPosessed() == 0);
		/*
		for (int i = 0; i < size; i++)
			if (get(i) == true)
				return false;
		return true;
		*/
	}

	public boolean isSet(int id) {
		if (id < 0 || id >= getSize())
			throw new IllegalArgumentException();
		return (mask.get(id / 8) & (1 << id % 8)) > 0;
	}

	public void set(int id) {
		if(isSet(id)) return;
		if (id < 0 || id >= getSize())
			throw new IllegalArgumentException();
		mask.put(id / 8, (byte) (mask.get(id / 8) | (1 << id % 8)));
		posessed++;
	}

	public int getSize() {
		return size;
	}
	
	public int getPosessed(){
		return posessed;
	}
	
	public boolean isComplete(){
		return getSize() == getPosessed();
	}

	public int getByteCount() {
		return mask.capacity();
	}

	public static int bytesRequiredForSize(int i) {
		int s = i / 8;
		if (s * 8 < i)
			++s;
		return s;
	}

	public ByteBuffer getByteBuffer() {
		ByteBuffer b = mask.asReadOnlyBuffer();
		b.rewind();
		return b;
	}
	
	public void saveToFile(String fileName){
		DiskResource.putFileContents(getByteBuffer(), fileName);
	}
	
	public static BitMask fromFile(String fileName, int chunkCount){
		return BitMask.parse(DiskResource.getFileContents(fileName), chunkCount);
	}

	public static String getDefaultFileName(String path, String fileName){
		return path + "/." + fileName + ".bmask";
	}
}
