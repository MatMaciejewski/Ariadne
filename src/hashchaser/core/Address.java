package hashchaser.core;

public class Address {
	private int[] data;
	private int port;
	
	private Address(int[] data, int port){
		this.data = data;
		this.port = port;
	}
	
	public static Address fromString(String address){
		if(address == null) throw new IllegalArgumentException();
		String[] parts;
		
		
		parts = address.split(":");
		if(parts.length > 2) throw new IllegalArgumentException();
		
		int port = (parts.length == 2) ? Integer.parseInt(parts[1]) : Application.DEFAULT_PORT;
		if(port < 1 || port > 65535) throw new IllegalArgumentException();
		
		parts = parts[0].split("\\.");
		if(parts.length != 4) throw new IllegalArgumentException();
		
		
		int[] d = new int[4];
		for(int i=0;i<4;++i){
			d[i] = Integer.parseInt(parts[i]);
			if(d[i] < 0 || d[i] > 255) throw new IllegalArgumentException();
		}
		return new Address(d, port);
	}
	
	public String toString(){
		return data[0] + "." + data[1] + "." + data[2] + "." + data[3];
	}
	
	public int getPort(){
		return port;
	}
}
