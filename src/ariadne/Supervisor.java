package ariadne;

import ariadne.data.Database;
import ariadne.data.Hash;

public class Supervisor extends Thread{
	private String name;
	private String path;
	private Hash hash;
	private boolean finished;
	
	
	public Supervisor(Hash hash, String path, String name){
		this.name = name;
		this.path = path;
		this.hash = hash;
		finished = false;
	}
	
	public void initialise(){
		
	}
	
	public void finalise(){
		
	}
	
	public void loop(){
		System.out.println("Downloading hash " + hash);
		try {
			sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("sleep interrupted");
		}
	}
	
	@Override
	public void run(){
		initialise();
		while(!finished){
			loop();
		}
		finalise();
	}
	
	
	public void halt(){
		finished = true;
	}
	
	public String getFileName(){
		return name;
	}
	
	public String getFilePath(){
		return path;
	}
	
	public Hash getHash(){
		return hash;
	}
	
	public boolean knownDescriptor(){
		return (Database.getFile(hash) != null);
	}
}
