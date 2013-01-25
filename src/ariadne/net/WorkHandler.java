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
		System.out.println("!!!!");
		
		
		TestState t;
		
		if(c.getState() == null){
			c.setState(new TestState());
		}
		t = (TestState) c.getState();
		
		t.counter++;
		
		if(t.counter < 5){
			System.out.println("ok...");
			return true;
		}
		
		System.out.println("limit!");
		return false;
	}

}
