package ariadne.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import ariadne.utils.Log;

/*
 * File Structure : Hash Name Filepath
 */
public class Settings {
	private static Hash[] hashes;
	private static String[] names;
	private static String[] filePath;

	public Settings() {
		BufferedReader br = null;
		try {
			String sCurrentLine;
			String temp = "";
			int i = 0, j = 0;
			br = new BufferedReader(new FileReader(".ariadneSettings"));
			while ((sCurrentLine = br.readLine()) != null) {
				while (sCurrentLine != " ") {
					temp += sCurrentLine.charAt(i);
					i++;
				}
				
				hashes[j] = new Hash(temp);
				
				temp = "";
				while (sCurrentLine != " ") {
					temp += sCurrentLine.charAt(i);
					i++;
				}
				names[j] = temp;
				temp = "";
				while (sCurrentLine != "\n") {
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
			if (hashTemp == hash)
				return names[j];
			j++;
		}
		return null;
	}
}
