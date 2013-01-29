package ariadne.protocol;

import java.nio.ByteBuffer;

import ariadne.data.Descriptor;
import ariadne.data.Hash;

/*
 * Byte 0-3		- Chunk count, or 0 if descriptor not found
 * Byte 4-7		- Chunk size in bytes
 * Byte 8-15	- File size
 * Byte 16-*	- Chunk hashes
 */

public class ResponseDescr extends Response {
	private boolean success;

	public Descriptor getDescriptor() {
		return (success) ? Descriptor.parse(getByteBuffer(), 0) : null;
	}

	@Override
	public boolean isComplete() throws InvalidMessageException {
		ByteBuffer buf = getByteBuffer();

		// ///////////////////////
		success = false;
		ByteBuffer b = getByteBuffer();

		if (b.limit() < 4)
			return false;

		int s = b.getInt(0);

		if (b.limit() == 4) {
			return (s == 0);
		} else {
			if (b.limit() >= 8) {
				if (b.getInt(4) < Descriptor.SMALLEST_ALLOWED_CHUNK_SIZE){
					throw new InvalidMessageException();
				}
			}
			if (b.limit() >= 16) {
				if (b.getInt(0) * b.getInt(4) < b.getLong(8)){
					throw new InvalidMessageException();
				}
			}
			success = (b.limit() == (s * Hash.LENGTH + 16));
		}
		return success;
	}

	public static ResponseDescr prepare(Descriptor d) {
		ResponseDescr r = new ResponseDescr();
		ByteBuffer b;
		if (d == null) {
			b = ByteBuffer.allocate(4);
			b.putInt(0);
		} else {
			ByteBuffer bd = d.getByteBuffer();
			b = ByteBuffer.allocate(bd.limit());
			b.put(bd);
		}

		r.setByteBuffer(b);
		return r;
	}
}
