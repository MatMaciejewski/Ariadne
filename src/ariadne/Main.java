package ariadne;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import ariadne.data.Hash;
import ariadne.net.Address;
import ariadne.net.Client;
import ariadne.net.Port;
import ariadne.protocol.Response;
import ariadne.protocol.ResponsePeers;



public class Main {

	public static void main(String[] args) {		
		
		Application.run(25566, 25566);
	}

}
