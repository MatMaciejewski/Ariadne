package ariadne.data;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ariadne.utils.Hexadecimal;
import ariadne.utils.Log;

public class Hash implements Comparable<Hash>{
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

	
	public byte[] getBytes() {
		return hash;
	}
	
	public Hash(ByteBuffer b, int offset){
		hash = new byte[LENGTH/2];
		b.position(offset);
		b.get(hash);
	}
	
	public static Hash computeFromString(String s){
		return Hash.computeFromBytes(s.getBytes());
	}
	
	public static Hash computeFromBytes(byte[] b){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(b, 0, b.length);
			return new Hash( md.digest() );
		} catch (NoSuchAlgorithmException e) {
			Log.error("Could not generate an MD5 hash!");
			throw new IllegalArgumentException();
		} catch (InvalidHashException e) {
			throw new IllegalArgumentException();
		}
	}
	
	// ///////////////////////////

	public String toString() {
		return Hexadecimal.toString(hash);
	}

	public boolean equals(Object obj) {
		if (obj instanceof Hash) {
			Hash h = (Hash) obj;
			for (int i = 0; i < LENGTH/2; ++i) {
				if (hash[i] != h.hash[i])
					return false;
			}
			return true;
		}
		
		return false;
	}

	@Override
	public int compareTo(Hash o) {
		int r = 0;
		for(int i=0;i<LENGTH/2;++i){
			r = hash[i] - o.hash[i];
			if(r != 0) return r;
		}
		return r;
	}
	
	@Override
	public int hashCode(){
		return toString().hashCode();
	}
}
