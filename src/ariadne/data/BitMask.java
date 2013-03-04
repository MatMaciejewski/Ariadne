package ariadne.data;

import java.nio.ByteBuffer;

public class BitMask extends ByteSource{
	private BitMask(){}
	private int chunks;
	
	public static BitMask emptyBitMask(int chunks){
		BitMask d = new BitMask();
		d.buf = ByteBuffer.allocate(bytesNeededForChunkCount(chunks));
		d.chunks = chunks;
		return d;
	}
	
	public static BitMask fromByteBuffer(ByteBuffer b, int chunks){
		if(b.remaining() != bytesNeededForChunkCount(chunks))
			throw new IllegalArgumentException();
		
		BitMask d = new BitMask();
		d.init(b, true);
		d.chunks = chunks;
		return d;
	}
	
	public static int bytesNeededForChunkCount(int chunks){
		if(chunks < 1) throw new IllegalArgumentException();
		int s = chunks/8;
		if(chunks%8 > 0) s += 1;
		return s;
	}
	
	public int getChunkCount(){
		return chunks;
	}
	
	public boolean get(int i){
		if(i >= chunks) throw new IllegalArgumentException();
		byte b = buf.get(i/8);
		byte m = (byte) (1 << (i%8));
		return ((b&m) != 0);
	}
	
	public void set(int i, boolean on){
		byte b = buf.get(i/8);
		byte m = (byte) (1 << (i%8));
		if(on){
			b = (byte) (b|m);
		}else{
			m = (byte) (255 - m);
			b = (byte) (b&m);
		}
		buf.put(i/8, b);
	}
}
