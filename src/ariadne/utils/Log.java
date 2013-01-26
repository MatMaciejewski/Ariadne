package ariadne.utils;

public class Log {
	public enum Level{
		NOTICE,
		WARNING,
		ERROR
	}
	
	public static void msg(Level lvl, String msg){
		System.out.println(lvl + ": " + msg);
	}
	
	public static void notice(String msg){
		msg(Level.NOTICE, msg);
	}
	
	public static void warning(String msg){
		msg(Level.WARNING, msg);
	}
	
	public static void error(String msg){
		msg(Level.ERROR, msg);
	}
}
