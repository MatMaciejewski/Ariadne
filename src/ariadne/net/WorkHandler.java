package ariadne.net;

import java.io.IOException;
import java.nio.ByteBuffer;

import ariadne.protocol.InvalidMessageException;
import ariadne.protocol.QueryBmask;
import ariadne.protocol.QueryChase;
import ariadne.protocol.QueryChunk;
import ariadne.protocol.QueryDescr;
import ariadne.protocol.QueryPeers;
import ariadne.protocol.Response;
import ariadne.utils.Log;

public class WorkHandler extends Worker {

	@Override
	public boolean handle(Conversation c) {

		State s;

		if (c.getState() == null) {
			s = new State();

			c.setState(s);
		} else {
			s = (State) c.getState();
		}

		try {

			ByteBuffer b = ByteBuffer.allocate(1024);
			int bytes_read = c.getSocket().read(b);
			System.out.println(bytes_read + " bytes read");
			for(int i=0;i<bytes_read;++i){
				System.out.print(b.get(i) + " ");
			}
			System.out.println(" ");

			if (b.limit() == 0)
				return false;
			// ///
			if (s.query == null) {
				byte code = b.get(0);
				System.out.println("CODE: " + code);
				switch (code) {
				case 0:
					s.query = new QueryPeers();
					break;
				case 1:
					s.query = new QueryChase();
					break;
				case 2:
					s.query = new QueryChunk();
					break;
				case 3:
					s.query = new QueryDescr();
					break;
				case 4:
					s.query = new QueryBmask();
					break;
				default:
					return false;
				}
			}
			b.flip();
			s.query.addByteBuffer(b);

			if (s.query.isComplete()) {
				Response r = s.query.respond();
				Log.notice("Valid messge received! " + r);
				c.getSocket().write(r.getByteBuffer());
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			Log.notice("IOException in WorkHandler");
			return false;
		} catch (InvalidMessageException e) {
			
			Log.notice("Invalid message received");
			ByteBuffer b = s.query.getByteBuffer();
			System.out.println("size: " + b.limit());
			for (int i = 0; i < b.limit(); ++i) {
				System.out.print((int) b.get(i) + " ");
			}
			System.out.println(" ");
			
			return false;
		} catch (Exception e) {
			Log.error("CRITICAL ERROR IN WorkHandler  -----------------------");
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		}
	}
}
