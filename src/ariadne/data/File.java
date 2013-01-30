package ariadne.data;

import java.nio.ByteBuffer;
import ariadne.utils.DiskResource;
import ariadne.utils.Log;

public class File {
	private Descriptor descriptor;
	private BitMask bitmask;
	private String name;
	private String path;

	public File(Descriptor descriptor, BitMask bitmask, String path, String name) {
		if (descriptor == null || bitmask == null || path == null
				|| name == null)
			throw new IllegalArgumentException();

		if (descriptor.getChunkCount() != bitmask.getSize())
			throw new IllegalArgumentException();

		this.descriptor = descriptor;
		this.bitmask = bitmask;
		this.path = path;
		this.name = name;

	}

	public String getDefaultFileName() {
		return path + "/" + name;
	}

	/**
	 * Clears the bitmask and reallocates disk memory
	 */
	public boolean reallocate() {
		DiskResource d = DiskResource.open(getDefaultFileName(), true);
		if (d != null) {
			if (d.resize(getDescriptor().getFileSize())) {
				d.close();
				this.bitmask = getDescriptor().getEmptyBitmask();
				return true;
			}
		}
		d.close();
		return false;
	}

	public boolean isAllocated() {
		DiskResource d = DiskResource.open(getDefaultFileName(), true);
		if (d != null) {
			if (d.getLength() == getDescriptor().getFileSize()) {
				d.close();
				return true;
			}
		}
		d.close();
		return false;
	}

	public Descriptor getDescriptor() {
		return descriptor;
	}

	public Chunk getChunk(int id) {
		if (getBitMask().isSet(id)) {
			return getChunkFromDisk(id, descriptor.getChunkSize(), descriptor
					.getChunkCount());
		} else
			return null;
	}

	public boolean setChunk(Chunk chunk, int id) {
		if (chunk.getSize() == descriptor.getChunkSize()) {
			if (chunk.getHash().equals(descriptor.getChunkHash(id))) {
				if (saveChunkToDisk(chunk, id)) {
					bitmask.set(id);
					return true;
				} else
					return false;

			} else {
				Log.error("Chunk's hash != descriptor's hash!");
			}
		}
		return false;
	}

	public String getFileName() {
		return name;
	}

	public String getFilePath() {
		return path;
	}

	public BitMask getBitMask() {
		return bitmask;
	}

	public void removeFile() {
		java.io.File in = new java.io.File(name);
		in.delete();
	}

	// //////////////////////////////////////////

	private Chunk getChunkFromDisk(int id, int chunkSize, int chunkCount) {
		DiskResource r = DiskResource.open(getDefaultFileName(), false);
		if(r != null){
			ByteBuffer b = r.read(chunkSize*id, chunkSize);
			r.close();
			return new Chunk(b);
		}
		return null;
	}

	private boolean saveChunkToDisk(Chunk c, int id){
		boolean success = false;
		int toWrite = descriptor.getChunkSize();
		if (id + 1 == descriptor.getChunkCount()) {
			toWrite -= (descriptor.getChunkCount()
					* descriptor.getChunkSize() - descriptor.getFileSize());
		}
		DiskResource r = DiskResource.open(getDefaultFileName(), false);
		if(r != null){
			ByteBuffer b = c.getByteBuffer();
			b.position(0);
			b.limit(toWrite);
			if(r.write(b, id * descriptor.getChunkSize())){
				success = true;
			}
			r.close();
		}
		
		return success;
	}
	
	public static File prepareExistingOne(String path, String name, int chunkSize){
		//										   ||
		//										   ||
		//										   ||
		//TODO: use this instead of the class here \/
		return null;
	}

	
	//This class returns outofmemoryerror for too large files
	public File(String path, String name, int chunkSize) {
		java.io.File fil = new java.io.File(path + "/" + name);
		this.path = path;
		this.name = name;
		System.out.println(path + "/" + name);
		if (fil != null) {
			ByteBuffer b;
			int fileLength = (int) fil.length() / chunkSize;
			b = ByteBuffer.allocate(4 + 4 + 8 + Hash.LENGTH
					* BitMask.bytesRequiredForSize((int) fil.length()));
			if ((int) fil.length() % chunkSize != 0)
				fileLength++;
			b.putInt(fileLength);
			b.putInt(chunkSize);
			b.putLong(fil.length());
			for (int i = 0; i < fileLength; i++) {
				Hash temp = getChunkFromDisk(i, chunkSize, fileLength)
						.getHash();
				b.put(temp.getByteBuffer());
			}
			Descriptor d = Descriptor.parse(b, 0);
			descriptor = d;
			BitMask bit = d.getEmptyBitmask();
			for (int i = 0; i < bit.getSize(); i++)
				bit.set(i);
			bitmask = bit;
			bit.saveToFile(BitMask.getDefaultFileName(path, name));
			d.saveToFile(Descriptor.getDefaultFileName(path, name));
			
			File f = new File(d, bit, path, name);
			Database.insertFile(f);
		}
	}
}
