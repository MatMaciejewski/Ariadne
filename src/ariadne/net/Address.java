package ariadne.net;

import java.net.Inet4Address;

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
}
