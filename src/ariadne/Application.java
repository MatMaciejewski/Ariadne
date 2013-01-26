package ariadne;

import java.nio.ByteBuffer;

<<<<<<< HEAD
import ariadne.data.Hash;
import ariadne.net.Port;
import ariadne.utils.TimedMultiMap;
=======
import ariadne.data.BitMask;
>>>>>>> ddf8b949b258f05fdcf6307ef4379c5050618b56

public class Application{
	
	
	
	public static void main(String[] args){
<<<<<<< HEAD
		Port alpha = new Port(8080);
		//System.out.println("8080".getBytes());
		System.out.println(alpha.getPort());
		
		/*TimedMultiMap<Hash, Double> m = new TimedMultiMap<Hash, Double>();
=======
>>>>>>> ddf8b949b258f05fdcf6307ef4379c5050618b56
		
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
		
<<<<<<< HEAD
		m.removeTimeouted(6);
		
		Set<Double> content = m.get(a, 5);
		
		System.out.println("content: " + content.size());
		
		for(Double v: content){
			System.out.println(v);
		}*/	
=======
>>>>>>> ddf8b949b258f05fdcf6307ef4379c5050618b56
	}
}