package ariadne.utils;

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
	 * @param fileName
	 *            File name
	 * @return a DiskResource or null
	 */
	public static DiskResource open(String fileName) {
		DiskResource r = new DiskResource();
		try {
			r.file = new RandomAccessFile(new java.io.File(fileName), "rw");
			return r;
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean close(){
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
	 * Resizes the file. If it is smaller than the given argument, it is expanded. If the value is lower, the file is truncated.
	 * @param size
	 * @return
	 */
	public boolean resize(long size){
		try {
			file.setLength(size);
			return true;
		} catch (IOException e) {
			return false;
		}
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
	 */
	public ByteBuffer read(int offset, int length) {
		try {
			ByteBuffer b = ByteBuffer.allocate(length);
			file.getChannel().read(b, offset);
			return b;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Reads all file data into a ByteBuffer. May return null if a file is too
	 * large.
	 * 
	 * @return ByteBuffer or null
	 */
	public ByteBuffer readAll() {
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
	 */
	public boolean write(ByteBuffer b, int offset) {
		try {
			file.getChannel().write(b, offset);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static ByteBuffer getFileContents(String fileName){
		ByteBuffer b = null;
		DiskResource r = DiskResource.open(fileName);
		if(r != null){
			b = r.readAll();
			if(b != null){
				b.flip();
			}
			r.close();
		}
		return b;
	}
	
	public static void putFileContents(ByteBuffer b, String fileName){
		DiskResource r = DiskResource.open(fileName);
		r.resize(b.remaining());
		r.write(b, 0);
		r.close();
	}
}
