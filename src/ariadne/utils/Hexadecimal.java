package ariadne.utils;

import javax.xml.bind.DatatypeConverter;

public class Hexadecimal {
	
	public static byte[] fromString(String s){
		return DatatypeConverter.parseHexBinary(s);
	}
	
	
	public static String toString(byte[] bytes){
		return DatatypeConverter.printHexBinary(bytes);
	}
}
