package ariadne.data;

public class File {	
	private Descriptor descriptor;
	private BitMask bitmask;
	private String fileName;
	
	
	public File(Descriptor descriptor, String fileName){
		this.descriptor = descriptor;
		this.fileName = fileName;
	}
	
	public Descriptor getDescriptor(){
		return descriptor;
	}
	
	public Chunk getChunk(int id){
		//Caching?
		return getChunkFromDisk(id);
	}
	public boolean setChunk(Chunk chunk, int id){
		if(chunk.getSize() == descriptor.getChunkSize()){
			if(chunk.getHash() == descriptor.getChunkHash(id)){
				saveChunkToDisk(chunk, id);
				return true;
			}
		}
		return false;
	}
	public String getFileName(){
		return fileName;
	}
	
	public BitMask getBitMask(){
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	////////////////////////////////////////////
	
	private Chunk getChunkFromDisk(int id){
		throw new UnsupportedOperationException("Not yet implemented");
	}
	private void saveChunkToDisk(Chunk c, int id){
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
