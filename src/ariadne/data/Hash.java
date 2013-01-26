package ariadne.data;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ariadne.utils.Hexadecimal;
import ariadne.utils.Log;

public class Hash {
	public static final int LENGTH = 32;
	private byte[] hash;

	public static class InvalidHashException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	public Hash(String s) throws InvalidHashException {
		try {
			hash = Hexadecimal.fromString(s);
		} catch (IllegalArgumentException e) {
			throw new InvalidHashException();
		}
	}

	public Hash(byte[] b) throws InvalidHashException {
		if(b.length != LENGTH/2) throw new InvalidHashException();
		hash = b.clone();
	}
<<<<<<< HEAD

	public byte[] getBytes() {
		return hash;
=======
	
	public Hash(ByteBuffer bb, int offset){
		byte[] hash = new byte[LENGTH/2];
		bb.get(hash, offset, hash.length);
	}
	
	public byte[] getBytes(){
			return hash;
>>>>>>> 8ec0204eda86205fef15f1e76dd6bab3913327d0
	}
	
	public static Hash computeFromString(String s) throws InvalidHashException{
		return Hash.computeFromBytes(s.getBytes());
	}
	
	public static Hash computeFromBytes(byte[] b) throws InvalidHashException{
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(b, 0, b.length);
			return new Hash( md.digest() );
		} catch (NoSuchAlgorithmException e) {
			Log.error("Could not generate an MD5 hash!");
			throw new InvalidHashException();
		}
	}

	// ///////////////////////////

	public String toString() {
		return Hexadecimal.toString(hash);
	}

	public boolean equals(Object obj) {
		if (obj instanceof Hash) {
			Hash h = (Hash) obj;
			for (int i = 0; i < LENGTH; ++i) {
				if (hash[i] != h.hash[i])
					return false;
			}
			return true;
		}
		return false;
	}
}
