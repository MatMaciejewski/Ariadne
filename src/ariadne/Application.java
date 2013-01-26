package ariadne;

import java.nio.ByteBuffer;

import ariadne.data.BitMask;

public class Application{
	
	
	
	public static void main(String[] args){
		
		BitMask b = new BitMask(18);
		
		System.out.println(b.get(5));
		b.set(5);
		b.set(8);
		System.out.println(b.get(5));
		
		
		ByteBuffer f = b.getByteBuffer();
		f.rewind();
		while(f.position() < f.limit()){
			System.out.println(f.get());
		}
		
	}
}