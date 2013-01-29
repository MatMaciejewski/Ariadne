package ariadne;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import ariadne.data.BitMask;
import ariadne.data.Catalogue;
import ariadne.data.Chunk;
import ariadne.data.Database;
import ariadne.data.Descriptor;
import ariadne.data.File;
import ariadne.data.Hash;
import ariadne.net.Address;
import ariadne.net.Client;
import ariadne.protocol.Response;
import ariadne.protocol.ResponseBmask;
import ariadne.protocol.ResponseChase;
import ariadne.protocol.ResponseChunk;
import ariadne.protocol.ResponseDescr;
import ariadne.protocol.ResponsePeers;
import ariadne.utils.Log;

public class Supervisor extends Thread {
	private String name;
	private String path;
	private Hash hash;

	public enum State {
		LOOKING_FOR_DESCRIPTOR, CHASING_CHUNKS, COMPLETE, ERROR
	}
	public class Pair{
		public Address peer;
		public BitMask bitmask;
	}

	private State currentState;
	private Queue<Address> noteworthy; // peers worth asking about other peers
	private Queue<Address> interested; // peers that also chase this hash
	private Queue<Address> checked; // peers having a subset of our chunks
	private Queue<Pair> seeders; // peers having something we do not have
	private Catalogue.Listener listener;
	private Descriptor descriptor;
	private File file;

	public Supervisor(Hash hash, String path, String name) {
		this.name = name;
		this.path = path;
		this.hash = hash;
		currentState = State.LOOKING_FOR_DESCRIPTOR;
		noteworthy = new LinkedList<Address>();
		interested = new LinkedList<Address>();
		checked = new LinkedList<Address>();
		seeders = new LinkedList<Pair>();
		listener = Catalogue.getListener(getHash());

		interested.addAll(Catalogue
				.getPeerForHash(getHash(), Integer.MAX_VALUE));
	}

	private void initialise() {
		// Look for the descriptor in the file system
		currentState = State.LOOKING_FOR_DESCRIPTOR;
		try {
			Descriptor d = Descriptor.parseFile(path + "/" + name + ".desc");
			// It looks like we have it.

			if (d.getHash().equals(getHash())) {
				// Yep, we do :)
				if (prepareNewFile(d)) {
					currentState = State.CHASING_CHUNKS;
				} else {
					// Something went terribly wrong
					currentState = State.ERROR;
				}
			}
		} catch (Exception e) {
			// Nope, descriptor not found. Gotta ask for it
		}

	}

	private void finalise() {

	}

	private boolean prepareNewFile(Descriptor d) {
		return Database.insertFile(d, d.getEmptyBitmask(), path, name, true);
	}

	private void lookForDescriptor() {
		Address peer;
		
		if(!interested.isEmpty()){
			peer = interested.poll();
			
			ResponseDescr response = Application.getClient().sendDescrQuery(peer, hash, 2000);
			if(response == null){
				//Returned message is incorrect - we forget about this guy
			}else{
				Descriptor d = response.getDescriptor();
				if(d == null){
					//Peer does not have this descriptor. But mabye he knows somebody who has it?
					noteworthy.add(peer);
				}else{
					//WE have a descriptor. But is it THE descriptor?
					if(d.getHash().equals(getHash())){
						if(prepareNewFile(d)){
							currentState = State.CHASING_CHUNKS;
						}else{
							currentState = State.ERROR;
						}
					}
				}
			}
		}else if(!noteworthy.isEmpty()){
			peer = noteworthy.poll();
			
			ResponseChase response = Application.getClient().sendChaseQuery(peer, hash, 2000);
			if(response == null){
				//Returned garbage - we forget about this guy
			}else{
				List<Address> peers = response.getPeers();
				
				int returned = 0;
				for(Address a: peers){
					if(a != peer && a != Application.getClient().getAddress()){
						noteworthy.add(a);	
						returned++;
					}
				}	
				
				if(response.isInterested()){
					if(returned > 0){
						checked.add(peer);
					}
				}
				
			}
		} else if(!checked.isEmpty()){
			noteworthy.addAll(checked);
			checked.clear();
			noteworthy.addAll(Catalogue.getRandomPeers(32));	
		}
	}

	private void lookForChunks() {
		Address peer;
		if(!seeders.isEmpty()){
			Pair p = seeders.poll();
			ResponseChunk r;
			Chunk c;
			
			//TODO: RANDOMIZE THIS
			for(int i=0;i<p.bitmask.getSize();++i){
				if(p.bitmask.get(i)){
					r = Application.getClient().sendChunkQuery(p.peer, hash, i, descriptor.getChunkSize(), 2000);
					if(r != null){
						c = r.getChunk();
						if(c != null){
							if(file.setChunk(c, i)){
								continue;
							}
						}
					}
				}
				noteworthy.add(p.peer);
				break;
			}
		} else if(!interested.isEmpty()){
			peer = interested.poll();
			
			
		}
	}

	@Override
	public void run() {
		initialise();

		Address peer;
		while (currentState != State.COMPLETE && currentState != State.ERROR) {

			while (true) {
				peer = listener.getNext();
				if (peer == null)
					break;
				else
					interested.add(peer);
			}

			if (currentState == State.LOOKING_FOR_DESCRIPTOR) {
				lookForDescriptor();
			} else {
				//lookForChunks();
			}
			
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		finalise();
	}

	public void halt() {
		currentState = State.COMPLETE;
	}

	public String getFileName() {
		return name;
	}

	public String getFilePath() {
		return path;
	}

	public Hash getHash() {
		return hash;
	}

	public boolean knownDescriptor() {
		return (Database.getFile(hash) != null);
	}
}
