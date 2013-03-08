package ariadne.data;

import java.nio.ByteBuffer;

public abstract class Descriptor extends ByteSource {
	private Integer hashCode;
	
	public Hash getHash(){
		return Hash.calculatedFromByteBuffer(buf);
	}
	
	public static Descriptor fromByteBuffer(ByteBuffer b){
		if(b.remaining() < 4) 
			return null;
		
		int v = b.getInt(0);
		if(v == 0) 
			return FolderDescriptor.fromByteBuffer(b);
		
		if(v > 0)
			return FileDescriptor.fromByteBuffer(b);
		else
			return null;
	}
	
	public int hashCode(){
		if(hashCode == null){
			hashCode = getBuffer().hashCode();
		}
		return hashCode;
	}
	public void invalidateHashCode(){
		hashCode = null;
	}
}
