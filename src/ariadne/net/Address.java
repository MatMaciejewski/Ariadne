package ariadne.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Address {
	public final static int BYTESIZE = Port.BYTESIZE + 4;
	private Inet4Address userIp;
	private Port userPort;
	
	public Address(String s, int port){
		try {
			userIp = (Inet4Address) InetAddress.getByName(s);
			userPort = new Port(port);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException();
		}
	}

	public Address(Inet4Address ip, Port port) {
		userIp = ip;
		userPort = port;
	}

	public Inet4Address getIpAddress() {
		return userIp;
	}

	public Port getPort() {
		return userPort;
	}

	public static Address fromByteBuffer(ByteBuffer b, int offset) {
		byte[] ip = new byte[4];

		b.position(offset);
		b.get(ip);

		Inet4Address addr;

		try {
			addr = (Inet4Address) InetAddress.getByAddress(ip);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException();
		}

		Port pt = new Port(b, 4);

		return new Address(addr, pt);
	}

	public ByteBuffer getByteBuffer() {
		ByteBuffer b = ByteBuffer.allocate(BYTESIZE);
		b.put(userIp.getAddress());
		b.put(userPort.getByteBuffer());
		b = b.asReadOnlyBuffer();
		b.rewind();
		return b;
	}
	
	public String toString(){
		return getIpAddress().toString() + ":" + getPort().getPort();
	}
}
