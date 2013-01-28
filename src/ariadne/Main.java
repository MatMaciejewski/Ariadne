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
		
		//Application.run(25566, 25566);
		
		
		
	

		Client c = new Client(new Address("127.0.0.1", 8080));
		
		Address a = new Address("192.168.1.111", 25566);
		
		Response r = c.sendChaseQuery(a, Hash.computeFromString("asd"), 2000);
		
		System.out.println("returned " + r);
		
		
		
	}

}
