package ariadne.data;

public class Descriptor {
	private Hash hash;
	private int chunkCount;
	private int chunkSize;
	
	public Descriptor(byte[] source){
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public Hash getHash(){
		return hash;
	}
	
	public int getChunkCount(){
		return chunkCount;
	}
	public int getChunkSize(){
		return chunkSize;
	}
	
	public byte[] getBytes(){
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public Hash getChunkHash(int id){
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
