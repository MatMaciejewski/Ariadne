package ariadne.db;

import java.io.File;

import ariadne.data.Hash;

public interface FileBase {
	//TODO: implement catalogue
	public File getFile(Hash hash);
}
