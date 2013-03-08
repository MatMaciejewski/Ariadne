package ariadne.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Address extends ByteSource{
	private static int LENGTH = 18;
	private Address(){}
	
	public static Address fromByteBuffer(ByteBuffer b){
		if(b.remaining() != LENGTH) return null;
		Address a = new Address();
		a.init(b, true);
		return a;
	}
	
	public static Address fromHostName(String addr, int port){
		try {
			InetAddress i = InetAddress.getByName(addr);
			Address a = new Address();
			byte[] data = i.getAddress();
			ByteBuffer b = ByteBuffer.allocate(18);
			b.position(14);
			b.putInt(port);
			if(data.length == 4){
				b.position(12);
			}else{
				b.position(0);
			}

			b.put(data);
			a.buf = b;
			return a;
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException();
		}
	}
	
	public InetAddress getInetAddress(){
		boolean ipv4 = true;
		for(int i=0;i<12;++i){
			if(buf.get(i) != 0){
				ipv4 = false;
				break;
			}
		}
		byte[] data;
		
		buf.position(0);
		if(ipv4){
			data = new byte[4];
		}else{
			data = new byte[16];
		}
		buf.get(data);
		try {
			return InetAddress.getByAddress(data);
		} catch (UnknownHostException e) {
			return null;
		}
	}
	
	public int getPort(){
		int l = (int)buf.get(16) << 8;
		int r = (int)buf.get(17);
		if(r < 0) r += 256;
		return l+r;
	}
}
