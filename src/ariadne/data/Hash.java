package ariadne.data;

import java.nio.ByteBuffer;

import ariadne.utils.Hexadecimal;

public class Hash {
	public static final int LENGTH = 32;
	private byte[] hash;

	public class InvalidHashException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	public Hash(String s) throws InvalidHashException {
		try {
			hash = Hexadecimal.fromString(s);
		} catch (IllegalArgumentException e) {
			throw new InvalidHashException();
		}
	}

	public Hash(byte[] b) {
		hash = b.clone();
	}

	
	public byte[] getBytes() {
		return hash;
	}
	
	public Hash(ByteBuffer bb, int offset){
		byte[] hash = new byte[LENGTH/2];
		bb.get(hash, offset, hash.length);
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
