package ariadne.protocol;

import java.nio.ByteBuffer;

public class ResponseDescr extends Response{

	@Override
		public boolean isComplete() throws InvalidMessageException {
			ByteBuffer b = getByteBuffer();
			if (b.limit() <= 8)
				return false;
			else {
				b.position(0);
				int count = b.getInt();
				int length = b.getInt();
					return (b.limit()==8+length*count);
			}
		}
}
