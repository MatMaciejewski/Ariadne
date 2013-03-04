package ariadne;

import java.nio.ByteBuffer;

public class Debug {
	public static void printBinary(ByteBuffer b){
		System.out.println("ByteBuffer: "+b);
		for(int i=0;i<b.capacity();++i){
			System.out.print(bin(b.get(i))+" ");
			if(i%8==7) System.out.println("");
		}
		if(b.capacity()%8!=0) System.out.println("");
		System.out.println("---------");
	}
	
	public static void printSigned(ByteBuffer b){
		System.out.println("ByteBuffer: "+b);
		for(int i=0;i<b.capacity();++i){
			System.out.print(b(b.get(i)));
			if(i%8==7) System.out.println("");
		}
		if(b.capacity()%8!=0) System.out.println("");
		System.out.println("---------");
	}
	
	public static void printUnsigned(ByteBuffer b){
		System.out.println("ByteBuffer: "+b);
		int t;
		for(int i=0;i<b.capacity();++i){
			t = b.get(i);
			if(t<0) t+=256;
			System.out.print(b(t));
			if(i%8==7) System.out.println("");
		}
		if(b.capacity()%8!=0) System.out.println("");
		System.out.println("---------");
	}
	
	public static void printHex(ByteBuffer b){
		System.out.println("ByteBuffer: "+b);
	    StringBuilder sb = new StringBuilder();
	    for(int i=0;i<b.capacity();++i){
	    	sb.append("   "+String.format("%02X", b.get(i)));
	    	if(i%8==7) sb.append("\n");
	    }
	    System.out.println(sb.toString());
	    System.out.println("---------");
	}
	
	public static String b(int b){
		String s = ""+b;
		while(s.length() < 5) s = " "+s;
		return s;
	}
	
	public static String bin(byte b){
		String s = "";
		for(int i=7;i>=0;--i){
			if((b & (1<<i)) == 0){
				s += "0";
			}else{
				s += "1";
			}
		}
		return s;
	}
}
