package ariadne.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Address {
	private Inet4Address userIp;
	private Port userPort;

	public Address(Inet4Address ip, Port port){
		userIp=ip;
		userPort=port;
	}
	public Inet4Address getIpAddress() {
		return userIp;
	}

	public Port getPort() {
		return userPort;
	}
	
	public void setPort(Port newPort){
		userPort=newPort;
	}
	
	public static Address fromByteBuffer(ByteBuffer b, int offset){
		//length is 6 (ip+port)
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
}
