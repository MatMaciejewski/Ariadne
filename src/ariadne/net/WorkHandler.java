package ariadne.net;

import java.io.IOException;
import java.nio.ByteBuffer;

public class WorkHandler extends Worker {

	@Override
	public boolean handle(Conversation c) {
		
		ByteBuffer b = ByteBuffer.allocate(1024);
		
		try {
			c.getSocket().read(b);
		} catch (IOException e) {
			System.out.println("ioex");
		}
		
		for(int i=0;i<b.position();++i){
			System.out.print((char) b.get(i));
		}
		
		
		return false;
	}

}
