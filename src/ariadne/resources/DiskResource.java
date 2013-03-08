package ariadne.resources;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class DiskResource {
	private RandomAccessFile file;

	private DiskResource() {
	}

	/**
	 * Creates a diskResource object or returns null if something goes wrong.
	 * 
	 * @param filepath
	 *            File name
	 * @return a DiskResource or null
	 * @throws IOException 
	 */
	public static DiskResource open(String filepath, boolean createIfNotExists) throws IOException {
		DiskResource r = new DiskResource();
		
		File f = new File(filepath);
		
		if(!f.exists()){
			if(createIfNotExists){
				f.createNewFile();
			}else{
				throw new IOException("Tried to open a file that does not exist");
			}
		}
		r.file = new RandomAccessFile(f, "rw");
		return r;
	}

	public boolean close() {
		try {
			file.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Get the total file length
	 * 
	 * @return length
	 */
	public long getLength() {
		try {
			return file.length();
		} catch (IOException e) {
			return -1;
		}
	}

	/**
	 * Resizes the file. If it is smaller than the given argument, it is
	 * expanded. If the value is lower, the file is truncated.
	 * 
	 * @param size
	 * @return
	 * @throws IOException 
	 */
	public void resize(long size) throws IOException {
		file.setLength(size);
	}

	/**
	 * Returns a new ByteBuffer (with read/write) with the specified data from a
	 * file.
	 * 
	 * @param offset
	 *            points where to start reading
	 * @param length
	 *            bytes to read
	 * @return a ByteBuffer or null
	 * @throws IOException 
	 */
	public ByteBuffer read(int offset, int length) throws IOException {
		ByteBuffer b = ByteBuffer.allocate(length);
		file.getChannel().read(b, offset);
		return b;
	}

	/**
	 * Reads all file data into a ByteBuffer. May return null if a file is too
	 * large.
	 * 
	 * @return ByteBuffer or null
	 * @throws IOException 
	 */
	public ByteBuffer readAll() throws IOException {
		if (getLength() > Integer.MAX_VALUE)
			return null;
		else
			return read(0, (int) getLength());
	}

	/**
	 * Writes a ByteBuffer in a specified place in memory. position() and
	 * limit() are taken into account here.
	 * 
	 * @param b
	 *            a buffer
	 * @param offset
	 *            where to start writing
	 * @return true if success, false otherwise
	 * @throws IOException 
	 */
	public void write(ByteBuffer b, int offset) throws IOException {
		file.getChannel().write(b, offset);
	}

	public void write(byte[] src, int srcOffset, int fileOffset, int length) throws IOException {
		file.seek(fileOffset);
		file.write(src, srcOffset, length);
	}

	public static ByteBuffer getFileContents(String filepath) throws IOException {
		ByteBuffer b = null;
		DiskResource r = DiskResource.open(filepath, false);
		if (r != null) {
			b = r.readAll();
			if (b != null) {
				b.flip();
			}
			r.close();
		}
		return b;
	}

	public static void putFileContents(String filepath, ByteBuffer b) throws IOException {
		DiskResource r = DiskResource.open(filepath, true);
		r.resize(b.remaining());
		r.write(b, 0);
		r.close();
	}

	public static void putFileContents(String filepath, int offset, int length, byte[] src) throws IOException {
		DiskResource r = DiskResource.open(filepath, true);
		
		r.resize(length);
		r.write(src, offset, 0, length);
		r.close();
	}
	
	public static void remove(String filepath) throws IOException{
		if(! new File(filepath).delete()){
			throw new IOException();
		}
	}
}
