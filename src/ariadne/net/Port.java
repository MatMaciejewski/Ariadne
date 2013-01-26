package ariadne.net;

import java.nio.ByteBuffer;

public class Port {
	private byte leftByte;
	private byte rightByte;
	public Port (byte rByte, byte lByte){
		leftByte=lByte;
		rightByte=rByte;
	}
	public Port(int port){
		rightByte = (byte) (port & 0xFF);
		leftByte = (byte)
		((port >> 8) & 0xFF);
	}
	
	public Port(ByteBuffer bb, int offset){
		byte[] ports = new byte[2];
		bb.get(ports, offset, ports.length);
		leftByte=ports[0];
		rightByte=ports[1];
	}
	public int getPort(){
		ByteBuffer bytes = ByteBuffer.wrap(new byte[] {0, 0, leftByte, rightByte});
	    return bytes.getInt();
		//return (rightByte << 8) | (leftByte & 0xFF); 
	}
	public byte[] getBytes(){
		byte[] bytes = new byte[2];
		bytes[0]=rightByte;
		bytes[1]=leftByte;
		return bytes;
	}	
}
