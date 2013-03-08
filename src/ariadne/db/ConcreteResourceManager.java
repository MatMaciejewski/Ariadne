package ariadne.db;

import java.io.IOException;
import java.nio.ByteBuffer;

import ariadne.data.BitMask;
import ariadne.data.Chunk;
import ariadne.data.Descriptor;
import ariadne.data.FileDescriptor;
import ariadne.resources.DiskResource;

public class ConcreteResourceManager implements ResourceManager{

	@Override
	public Descriptor getDescriptor(String filepath) {
		try {
			ByteBuffer b = DiskResource.getFileContents(filepath);
			return Descriptor.fromByteBuffer(b);
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public boolean saveDescriptor(String filepath, Descriptor d) {
		try{
			DiskResource.putFileContents(filepath, d.getBuffer());
			return true;
		}catch(IOException e){
			return false;
		}
	}

	@Override
	public BitMask getBitMask(String filepath) {
		try{
			ByteBuffer b = DiskResource.getFileContents(filepath);
			BitMask m = BitMask.fromByteBuffer(b);
			return m;
		}catch(IOException e){
			return null;
		}
	}

	@Override
	public boolean saveBitMask(String filepath, BitMask b) {
		try{
			DiskResource.putFileContents(filepath, b.getBuffer());
			return true;
		}catch(IOException e){
			return false;
		}
	}

	@Override
	public Chunk getChunk(String filepath, int offset, int length) {
		try{
			DiskResource r = DiskResource.open(filepath, false);
			ByteBuffer b = r.read(offset, length);
			Chunk c = Chunk.fromByteBuffer(b);
			return c;
		}catch(IOException e){
			return null;
		}
	}

	@Override
	public boolean saveChunk(String filepath, int offset, Chunk c) {
		try{
			DiskResource r = DiskResource.open(filepath, false);
			r.write(c.getBuffer(), offset);
			return true;
		}catch(IOException e){
			return false;
		}
	}

	@Override
	public boolean allocateFile(String filepath, FileDescriptor d) {
		try {
			DiskResource.open(filepath, true);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean removeResource(String filepath) {
		try {
			DiskResource.remove(filepath);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean createFolder(String folderpath) {
		java.io.File f = new java.io.File(folderpath);
		if(f.isDirectory()){
			return true;
		}else{
			return f.mkdirs();
		}
	}

}
