package ariadne.db;

import ariadne.data.BitMask;
import ariadne.data.Chunk;
import ariadne.data.Descriptor;
import ariadne.data.Hash;

/**
 * @author eipifi
 * Manages all the application data. 
 * Responsible for storing it on the hdd and retrieving during startup.
 */
public interface Database {
	public Descriptor getDescriptor(Hash h);
	public void addDescriptor(Descriptor d);
	public void removeDescriptor(Hash h);
	
	public BitMask getBitMask(Hash h);
	public void addBitMask(Hash h, BitMask b);
	public void removeBitMask(Hash h);
	
	public Chunk getChunk(String filepath, int id, int size);
	public void setChunk(String filepath, int id, Chunk c);
}
