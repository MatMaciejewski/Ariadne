package ariadne;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import ariadne.data.Descriptor;
import ariadne.data.File;
import ariadne.data.Hash;
import ariadne.net.Address;
import ariadne.net.Client;
import ariadne.net.Port;
import ariadne.protocol.Response;
import ariadne.protocol.ResponsePeers;



public class Main {

	public static void main(String[] args) {		
		
		//Application.run(25566, 25566);
		new File("C:\\Users\\Prophet\\Pulpit\\Ariadne","testowy.txt",16);
		Descriptor c = Descriptor.parseFile("testowy.txt.desc");
		
		for(int i=0;i<c.getChunkCount();i++)
			System.out.println(c.getChunkHash(i));
		System.out.println(c.getChunkCount());
		System.out.println(c.getChunkSize());
		System.out.println(c.getFileSize());
	}

}
