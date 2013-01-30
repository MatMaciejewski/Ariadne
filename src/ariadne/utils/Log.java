package ariadne.utils;

import java.nio.ByteBuffer;

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
	
	public static void print(ByteBuffer b, int offset){
		int pos = b.position();
		int lim = b.limit();
		
		b.position(offset);
		int i = 0;
		System.out.println("ByteBuffer " + b);
		System.out.println("------------------------------------------**S");
		while(b.hasRemaining()){
			System.out.print( pad("" + b.get(), 3) + " | ");
			if(i++ % 8 == 7) System.out.println("");
		}
		b.position(pos);
		b.limit(lim);
		System.out.println("");
		System.out.println("------------------------------------------**E");
	}
	
	public static String pad(String s, int len){
		String result = s;
		while(result.length() < len){
			result = result+" ";
		}
		return result;
	}
}
