package ariadne.db;

import ariadne.data.BitMask;
import ariadne.data.Chunk;
import ariadne.data.FileDescriptor;

/**
 * @author eipifi
 * Acts as a handle for file resources. Manages all information related to specific file.
 */
public interface File {
	public String getName();
	public String getPath();
	public FileDescriptor getDescriptor();
	public BitMask getBitMask();
}