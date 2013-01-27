package ariadne.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import ariadne.utils.Log;

/*
 * File Structure : 
 * Files quantity (int)
 * Hash Name Filepath xX
 */
public class Settings {
	private static Hash[] hashes;
	private static String[] names;
	private static String[] filePaths;
	private static int quantity;

	public Settings() {
		BufferedReader br = null;
		try {
			String sCurrentLine;
			String temp = "";
			int i = 0, j = 0;
			br = new BufferedReader(new FileReader(".ariadneSettings"));
			sCurrentLine = br.readLine();
			quantity = Integer.parseInt(sCurrentLine);
			hashes = new Hash[quantity];
			names = new String[quantity];
			filePaths = new String[quantity];
			while ((sCurrentLine = br.readLine()) != null) {
				i = 0;
				while (sCurrentLine.charAt(i) != ' ') {
					temp += sCurrentLine.charAt(i);
					i++;
				}
				hashes[j] = new Hash(temp);

				temp = "";
				i++;
				while (sCurrentLine.charAt(i) != ' ') {
					temp += sCurrentLine.charAt(i);
					i++;
				}
				names[j] = temp;
				temp = "";
				i++;
				while (i < sCurrentLine.length()) {
					temp += sCurrentLine.charAt(i);
					i++;
				}
				filePaths[j] = temp;
				temp = "";
				j++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
	}

	public Hash[] getAllHash() {
		return hashes;
	}

	public String getNameForHash(Hash hash) {
		int j = 0;
		for (Hash hashTemp : hashes) {
			if (hash.compareTo(hashTemp) == 0)
				return names[j];
			j++;
		}
		return null;
	}

	public String getPathForHash(Hash hash) {
		int j = 0;
		for (Hash hashTemp : hashes) {
			if (hash.compareTo(hashTemp) == 0)
				return filePaths[j];
			j++;
		}
		return null;
	}

	public void updateSettings(Hash hash, String name, String filePath) {
		if (getNameForHash(hash) == null) {
			Hash[] newHash = new Hash[quantity + 1];
			String[] newNames = new String[quantity + 1];
			String[] newPaths = new String[quantity + 1];
			for (int i = 0; i < quantity; i++) {
				newHash[i] = hashes[i];
				newNames[i] = names[i];
				newPaths[i] = filePaths[i];
			}
			newHash[quantity] = hash;
			newNames[quantity] = name;
			newPaths[quantity] = filePath;
			quantity++;
			hashes = newHash;
			names = newNames;
			filePaths = newPaths;
		}
	}

	/*
	 * -1 -> error 0 -> nothing new to add 1 -> success
	 */
	public int updateSettingsFile() {
		BufferedReader br = null;
		try {
			String sCurrentLine;
			String temp = "";
			RandomAccessFile byteFile = new RandomAccessFile(new java.io.File(
					".ariadneSettings"), "rws");
			sCurrentLine = byteFile.readLine();
			int fileQuantity = Integer.parseInt(sCurrentLine);
			if (fileQuantity == quantity)
				return 0;
			else {
				int startingPosition = (int) byteFile.length();
				byteFile.seek(startingPosition);
				for (int i = fileQuantity; i < quantity; i++) {
					byteFile.write(new String(hashes[i] + " " + names[i] + " "
							+ filePaths[i] + "\n").getBytes());
				}
				byteFile.close();
				return 1;
			}
		} catch (IOException e) {
			Log.error("File .ariadneSettings couldn't be opened");
			return -1;
		}
	}

}
