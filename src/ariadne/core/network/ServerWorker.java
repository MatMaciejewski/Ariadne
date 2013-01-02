package ariadne.core.network;


import java.io.IOException;

import ariadne.core.network.SocketManager.Conversation;
import ariadne.core.protocol.Message;
import ariadne.core.protocol.MessageParser;
import ariadne.core.protocol.Message.InvalidMessageException;

public class ServerWorker extends AbstractServerWorker {

	private static final int BUFFER_LENGTH = 1024;
	private byte[] buffer;

	public ServerWorker() {
		buffer = new byte[BUFFER_LENGTH];
	}

	private Message readMessage(Conversation c) throws IOException, InvalidMessageException {
		MessageParser parser = c.getState().parser;
		Message msg = null;

		int bytes_read = BUFFER_LENGTH;
		while (bytes_read == BUFFER_LENGTH && msg == null) {
			bytes_read = c.input().read(buffer, 0, BUFFER_LENGTH);
			parser.addData(buffer, bytes_read);
			msg = parser.getMessage();
		}

		return msg;
	}
	
	private void handleMessage(Message msg, Conversation c){
		System.out.println("Received CORRECT message! Type=" + msg.getType());
		c.close();
	}

	@Override
	public void work(Conversation c) {
		
		if (c.getState() == null)
			c.setState(new ConversationState());
		
		Message msg = null;
		try{
			msg = readMessage(c);
			if(msg == null){
				await(c);
				return;
			}
		}catch(IOException e){
			System.out.println("IO exception occured.");
			c.close();
			return;
		} catch (InvalidMessageException e) {
			System.out.println("Received message is invalid. Closing.");
			c.close();
			return;
		}
		handleMessage(msg, c);
	}

	/*
	 * @Override public void work(Conversation c) { System.out.println("Thread "
	 * + this.getId() + " handles conversation " + c.id());
	 * 
	 * if(c.getState() == null){ c.setState( new ConversationState() );
	 * c.getState().type = 0; }
	 * 
	 * try{ int bytes_read = c.input().read(buffer, 0, BUFFER_LENGTH);
	 * if(bytes_read <= 0) throw new IOException();
	 * 
	 * System.out.println("Data packet #" + (++c.getState().type));
	 * System.out.println("Bytes read: " + bytes_read); for(int
	 * i=0;i<bytes_read;++i){ System.out.println("Byte " + i + ": " +
	 * (int)buffer[i]); }
	 * 
	 * if(bytes_read == 3 && buffer[0] == 113 && buffer[1] == 13 && buffer[2] ==
	 * 10){ System.out.println("Conversation " + c.id() + " closed.");
	 * c.close(); }else{ c.output().write(buffer); await(c); }
	 * 
	 * }catch( IOException e ){ //e.printStackTrace(); c.close();
	 * System.out.println("Conversation closed due to an IO Exception."); }
	 * 
	 * 
	 * }
	 */
}
