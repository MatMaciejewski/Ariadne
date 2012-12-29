package hashchaser.core.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import hashchaser.core.Address;
import hashchaser.core.protocol.Message;

public class Finder {
	public static final int BUFFER_LENGTH = 128;
	public static boolean ask(Message query, Address address, Message response, int timeout){
		Socket client;
		try {
			client = new Socket(address.toString(), address.getPort());
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		try {
			long time = System.currentTimeMillis();
			long tmp;
			client.setSoTimeout(timeout);
			client.getOutputStream().write( query.getBytes() );
			InputStream input = client.getInputStream();
			int bytes_read;
			byte[] buffer = new byte[BUFFER_LENGTH];
			
			while(!response.isComplete()){
				tmp = time+timeout-System.currentTimeMillis();
				if(tmp<=0) throw new IOException(); 
				client.setSoTimeout((int) tmp);
				bytes_read = input.read(buffer, 0, BUFFER_LENGTH);
				if(bytes_read <= 0) throw new IOException();
				response.addData(buffer, bytes_read);
			}
		} catch (Exception e) {
			try {
				client.close();
			} catch (IOException e1) {}
			return false;
		}
		try {
			client.close();
		} catch (IOException e1) {
			return false;
		}
		return true;
	}
}
