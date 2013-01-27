package ariadne.protocol;

import java.nio.ByteBuffer;

import ariadne.data.BitMask;

public class ResponseBmask extends Response{
	private int size;
	
	public ResponseBmask(int size){
		this.size = size;
	}
	
	public BitMask getBitMask(){
		return new BitMask(getByteBuffer(), size);
	}

	@Override
	public boolean isComplete() throws InvalidMessageException {
		ByteBuffer b = getByteBuffer();
		int s = BitMask.bytesRequiredForSize(size);
		
		if(b.capacity() > s) throw new InvalidMessageException();
		
		return (b.capacity() == s);
	}
}
