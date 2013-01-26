package ariadne.data;

public class Chunk {
	private Hash hash;
	private byte[] content;
	
	public Chunk(byte[] source){
		content=source;
		hash=new Hash(source);
		System.out.println(hash.toString());
	}
	
	public Hash getHash(){
		return hash;
	}
	
	public int getSize(){
		return content.length;
	}

	public byte[] getBytes(){
		return content;
	}
}
