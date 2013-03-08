package ariadne.db;

import ariadne.data.BitMask;
import ariadne.data.Chunk;
import ariadne.data.Descriptor;
import ariadne.data.FileDescriptor;

/**
 * @author eipifi
 *	Responsible for all disk-related instructions
 */

public interface ResourceManager {
	public boolean allocateFile(String filepath, FileDescriptor d);
	public boolean createFolder(String folderpath);
	
	public Descriptor getDescriptor(String filepath);
	public boolean saveDescriptor(String filepath, Descriptor d);
	
	public BitMask getBitMask(String filepath);
	public boolean saveBitMask(String filepath, BitMask b);
	
	public Chunk getChunk(String filepath, int offset, int length);
	public boolean saveChunk(String filepath, int offset, Chunk c);
	
	public boolean removeResource(String filepath);
}
