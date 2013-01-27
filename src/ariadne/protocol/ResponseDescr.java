package ariadne.protocol;

import java.nio.ByteBuffer;

import ariadne.data.Descriptor;
import ariadne.data.Hash;

/*
 * Byte 0-3		- Chunk count, or 0 if descriptor not found
 * Byte 4-7		- Chunk size in bytes
 * Byte 8-*		- Chunk hashes
 */

public class ResponseDescr extends Response{
	
	public Descriptor getDescriptor(){
		return Descriptor.parse(getByteBuffer(), 0);
	}

	@Override
	public boolean isComplete() throws InvalidMessageException {
		ByteBuffer b = getByteBuffer();
		
		if(b.limit() < 4) return false;
		
		int s = b.getInt(0);
		
		if(b.limit() == 4){
			return (s == 0);
		}else{
			if(b.limit() >= 8){
				if(b.getInt(4) < Descriptor.SMALLEST_ALLOWED_CHUNK_SIZE)
					throw new InvalidMessageException();
			}
			return (b.limit() == (s*Hash.LENGTH + 8));
		}
	}
	
	public static ResponseDescr prepare(Descriptor d){
		ResponseDescr r = new ResponseDescr();
		ByteBuffer b;
		if(d == null){
			b = ByteBuffer.allocate(4);
			b.putInt(0);
		}else{
			ByteBuffer bd = d.getByteBuffer();
			b = ByteBuffer.allocate(bd.limit());
			b.put(bd);
		}
		
		r.setByteBuffer(b);
		return r;
	}
}
