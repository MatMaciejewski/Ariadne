package ariadne.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import ariadne.utils.Log;

/*
 * File Structure : 
 * Files quantity (int)
 * Hash Name Filepath xX
 */
public class Settings {
	private static Hash[] hashes;
	private static String[] names;
	private static String[] filePath;
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
			filePath = new String[quantity];
			while ((sCurrentLine = br.readLine()) != null) {
				i=0;
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
				while (i<sCurrentLine.length()) {
					temp += sCurrentLine.charAt(i);
					i++;
				}
				filePath[j] = temp;
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
			System.out.println("J: "+j+" hash: "+hash+" Hashnao: "+hashTemp);
			if (hash.compareTo(hashTemp)==0) return names[j];
			j++;
		}
		return null;
	}
	
	public String getPathForHash(Hash hash) {
		int j = 0;
		for (Hash hashTemp : hashes) {
			System.out.println("J: "+j+" hash: "+hash+" Hashnao: "+hashTemp);
			if (hash.compareTo(hashTemp)==0) return filePath[j];
			j++;
		}
		return null;
	}
}
