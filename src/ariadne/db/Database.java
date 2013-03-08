package ariadne.db;

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
}
