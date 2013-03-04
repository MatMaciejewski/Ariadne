package ariadne.data;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash extends ByteSource{
	public static final int LENGTH = 32;
	private Hash(){}
	
	/**
	 * Creates a new hash by copying the given buffer.
	 * @param src buffer to be copied
	 * @return new hash
	 */
	public static Hash fromByteBuffer(ByteBuffer src){
		if(src.remaining() != LENGTH) throw new IllegalArgumentException();
		Hash h = new Hash();
		h.init(src, true);
		return h;
	}

	/**
	 * Calculates a new hash using the hashfunction with the given bytebuffer as input
	 * @param b buffer to be used as input
	 * @return new hash
	 */
	public static Hash calculatedFromByteBuffer(ByteBuffer b){
		MessageDigest d = getDigest();
		d.update(b);
		return fromDigest(d);
	}
	
	/**
	 * Calculates a new hash using the hashfunction with the given string as input
	 * @param s string to be used as input
	 * @return new hash
	 */
	public static Hash calculatedFromString(String s){
		MessageDigest d = getDigest();
		d.update(s.getBytes());
		return fromDigest(d);
	}
	
	private static MessageDigest getDigest(){
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Hash function not available");
		}
	}
	
	private static Hash fromDigest(MessageDigest digest){
		byte[] data = digest.digest();
		Hash h = new Hash();
		assert(data.length == LENGTH);
		h.init(data, false);
		return h;
	}
}
