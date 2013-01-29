package ariadne.protocol;

import java.nio.ByteBuffer;

import ariadne.data.BitMask;

public class ResponseBmask extends Response {
	private int size;
	private boolean success;

	public ResponseBmask(int size) {
		this.size = size;
		success = false;
	}

	public BitMask getBitMask() {
		return (success) ? new BitMask(getByteBuffer(), 1, size) : null;
	}

	public boolean hasBitMask() {
		return success;
	}

	@Override
	public boolean isComplete() throws InvalidMessageException {
		ByteBuffer b = getByteBuffer();
		
		if (b.limit() == 1) {
			if (b.get(0) == 0)
			return true;
		}

		int s = 1 + BitMask.bytesRequiredForSize(size);

		if (b.limit() > s)
			throw new InvalidMessageException();

		if (b.limit() == s)
			success = true;
		return success;
	}
	
	
	private ResponseBmask(){}
	
	public static ResponseBmask prepare(BitMask b){
		ResponseBmask r = new ResponseBmask();
		ByteBuffer bt;
		if(b == null){
			r.success = false;
			bt = ByteBuffer.allocate(1);
			bt.put((byte) 0);
			
		}else{
			r.success = true;
			bt = ByteBuffer.allocate(1 + b.getByteCount());
			bt.put((byte) 1);
			bt.put(b.getByteBuffer());
		}
		r.setByteBuffer(bt);
		return r;
	}
}
