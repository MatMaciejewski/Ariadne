package ariadne.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;

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

	public static void initlialise() {
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

	public static List<Hash> getAllHash() {
		List<Hash> list = new LinkedList<Hash>();
		for(Hash h: hashes){
			list.add(h);
		}
		return list;
	}
	public static String getNameForHash(Hash hash) {

		int j = 0;
		
		for (Hash hashTemp : hashes) {
			if (hash.equals(hashTemp)){
				return names[j];
			}
				
			j++;
		}
		return null;
	}

	public static String getPathForHash(Hash hash) {
		int j = 0;
		for (Hash hashTemp : hashes) {
			if (hash.equals(hashTemp))
				return filePaths[j];
			j++;
		}
		return null;
	}

	public static void updateSettings(Hash hash, String name, String filePath) {
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
	public static int updateSettingsFile() {
		try {
			String sCurrentLine;
			RandomAccessFile byteFile = new RandomAccessFile(new java.io.File(
					".ariadneSettings"), "rws");
			sCurrentLine = byteFile.readLine();
			int fileQuantity = Integer.parseInt(sCurrentLine);
			if (fileQuantity == quantity)
				return 0;
			else {
				int startingPosition = (int) byteFile.length();
				byteFile.seek(0);
				byteFile.write(new String(Integer.toString(quantity))
						.getBytes());
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
