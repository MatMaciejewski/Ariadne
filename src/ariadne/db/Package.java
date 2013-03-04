package ariadne.db;

import ariadne.data.BitMask;
import ariadne.data.Chunk;
import ariadne.data.Descriptor;

public interface Package {
	//TODO: implement package
	public Descriptor getDescriptor();
	public BitMask getBitMask();
	public Chunk getChunk(int i);
}
