package ariadne.net;

import java.io.IOException;
import java.net.Socket;
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
			c.getSocket().read(b);

			if (b.limit() == 0)
				return false;
			if (s.query == null) {
				byte code = b.get(0);
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
				Address a;
				try{
					Socket sock = c.getSocket().socket();
					a = new Address(sock.getInetAddress().getHostAddress(), sock.getPort());
					s.query.setAuthor(a);
				} catch(Exception e){
					return false;
				}
				Log.notice("Received message " + s.query.getCode() + " from " + a);
				Response r = s.query.respond();
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
			return false;
		} catch (Exception e) {
			Log.error("CRITICAL ERROR IN WorkHandler  -----------------------");
			e.printStackTrace();
			return false;
		}
	}
}
