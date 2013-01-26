package ariadne.data;

import java.util.HashSet;

import ariadne.data.Hash.InvalidHashException;

public class Descriptor {
	private Hash hash;
	private int chunkCount;
	private int chunkSize;
	private HashSet<Hash> chunkCollection;

	public Descriptor(byte[] source) {
		String src = new String(source);
		String temp = new String("");
		chunkCount = -1;
		chunkSize = -1;
		hash = null;
		chunkCollection = new HashSet<Hash>();
		int i = 0;
		while (i < src.length()) {
			while (src.charAt(i) != '\n') {
				temp += src.charAt(i);
				i++;
			}
			if (hash == null) {
					try {
						hash = new Hash(temp);
					} catch (InvalidHashException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			} else if (chunkCount == -1) {
				chunkCount = Integer.parseInt(temp);
			} else if (chunkSize == -1) {
				chunkSize = Integer.parseInt(temp);
			} else {
					try {
						chunkCollection.add(new Hash(temp));
					} catch (InvalidHashException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			temp = "";
			if(i<src.length()) i++;
		}
	}

	public Hash getHash() {
		return hash;
	}

	public int getChunkCount() {
		return chunkCount;
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public byte[] getBytes() {
		String out = new String(hash.toString() + "\n"
				+ Integer.toString(chunkCount) + "\n"
				+ Integer.toString(chunkSize) + "\n");
		for(Hash temp : chunkCollection){
			out+=temp.toString()+"\n";
		}
		return out.getBytes();
	}

	public Hash getChunkHash(int id) {
		int i=0;
		for(Hash it : chunkCollection){
			if(i==id){
				return it;
			}
			i++;
		}
		return null;
	}
}
