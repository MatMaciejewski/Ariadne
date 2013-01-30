package ariadne.data;

import java.util.Properties;

public class BetterSettings {
	private static class Pair{
		public String name;
		public String path;
	}
	public static final String file = "/.ariadne_settings";
	private static Properties data;
	
	
	public static void load(){
		data = new Properties();
		java.io.File f = new java.io.File(file);
		//TODO: 
	}
	
	public static void save(){
		
	}
}
