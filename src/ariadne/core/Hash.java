package ariadne.core;

public class Hash {
	public class InvalidHashException extends Exception{
		private static final long serialVersionUID = 1L;
	}
	
	public static final int LENGTH = 16;
	private byte[] data;
	
	private void init(){
		data = new byte[LENGTH];
	}
	
	public byte at(int i){
		return data[i];
	}
	
	public Hash(Byte[] hash, int offset){
		init();
		for(int i=0;i<LENGTH;++i){
			data[i] = hash[i+offset];
		}
	}
	
	public Hash(Byte[] hash){
		init();
		for(int i=0;i<LENGTH;++i){
			data[i] = hash[i];
		}
	}
	
	public Hash(String s) throws InvalidHashException{
		init();
		byte[] hash = s.getBytes();
		if(hash.length != LENGTH*2) throw new InvalidHashException();
		int val;
		
		
		for(int i=0;i<LENGTH;++i){
			val = (hexToVal(hash[2*i])*16 + hexToVal(hash[2*i+1]));
			data[i] = (byte) (val);
		}
	}
	
	private int hexToVal(byte a) throws InvalidHashException{
		if( 48 <= a && a <= 57){
			return (a-48);
		}else if(65 <= a && a <= 70){
			return (a-65+10);
		}else if(97 <= a && a <= 102){
			return (a-97+10);
		}else throw new InvalidHashException();
	}
	private byte valToChar(byte val){
		if(val < 10) return (byte) (val+48);
		else return (byte) (val+87);
	}
	
	public String toString(){
		byte[] str = new byte[LENGTH*2];
		int val;
		for(int i=0;i<LENGTH;++i){
			val = (data[i]<0) ? data[i]+256 : data[i];
			str[2*i+0] = valToChar((byte) (val/16));
			str[2*i+1] = valToChar((byte) (val%16));
		}
		return new String(str);
	}
	
	public byte[] getBytes(){
		return data;
	}
}
