package ariadne;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ariadne.data.BitMask;
import ariadne.data.Catalogue;
import ariadne.data.Chunk;
import ariadne.data.Database;
import ariadne.data.Descriptor;
import ariadne.data.File;
import ariadne.data.Hash;
import ariadne.net.Address;
import ariadne.protocol.ResponseBmask;
import ariadne.protocol.ResponseChase;
import ariadne.protocol.ResponseChunk;
import ariadne.protocol.ResponseDescr;
import ariadne.utils.Log;

public class Supervisor extends Thread {
	public enum State {
		LOOKING_FOR_DESCRIPTOR, CHASING_CHUNKS, COMPLETE, ERROR, HALT
	}

	public class Pair {
		public Address peer;
		public BitMask bitmask;
		public int toCheck;
	}

	private State currentState;
	private Catalogue.Listener listener;
	private String name;
	private String path;
	private Hash hash;
	private File file;

	private Queue<Address> noteworthy; // peers worth asking about other peers
	private Queue<Address> interested; // peers that also chase this hash, we get their info from catalogue
	private Queue<Address> checked; // peers having a subset of our chunks
	private Queue<Pair> seeders; // peers having something we do not have

	private Supervisor() {
	}

	public static Supervisor forCompleteFile(File completeFile) {
		Supervisor s = new Supervisor();
		if (completeFile.getBitMask().isComplete()) {
			s.file = completeFile;
			s.currentState = State.COMPLETE;
			s.name = s.file.getFileName();
			s.path = s.file.getFilePath();
			s.hash = s.file.getDescriptor().getHash();
			s.listener = Catalogue.getListener(s.getHash());
			return s;
		} else {
			return null;
		}
	}

	public static Supervisor forPartialFile(Hash hash, String path, String name) {
		Supervisor s = new Supervisor();
		s.currentState = State.LOOKING_FOR_DESCRIPTOR;
		s.name = name;
		s.path = path;
		s.hash = hash;
		s.listener = Catalogue.getListener(s.getHash());

		s.file = Database.getFile(hash);
		if (s.file != null) {
			s.currentState = State.CHASING_CHUNKS;
			// cool, we have everything
			return s;
		}

		Descriptor d = Descriptor.fromFile(Descriptor.getDefaultFileName(path,
				name));
		if (d != null) {
			BitMask b = BitMask.fromFile(
					BitMask.getDefaultFileName(path, name), d.getChunkCount());

			boolean completed;
			if (b != null) {
				// We have everything, just start looking for chunks
				completed = s.continueFile(d, b);
			} else {
				// We have only the descriptor
				completed = s.prepareNewFile(d);
			}
			if (!completed) {
				s.currentState = State.ERROR;
				s.file = null;
			}
			Database.insertFile(s.file);
		} else {
			// We have no descriptor. Therefore:
			s.file = null;
			s.currentState = State.LOOKING_FOR_DESCRIPTOR;
		}
		return s;
	}

	private void initialise() {
		noteworthy = new LinkedList<Address>();
		interested = new LinkedList<Address>();
		checked = new LinkedList<Address>();
		seeders = new LinkedList<Pair>();
		interested.addAll(Catalogue
				.getPeerForHash(getHash(), Integer.MAX_VALUE));
	}

	private void finalise() {
		if (file != null) {
			if(!file.getDescriptor().saveToFile(Descriptor.getDefaultFileName(path, name))){
				System.out.println("Descriptor not saved");
			}
			if(!file.getBitMask().saveToFile(BitMask.getDefaultFileName(path, name))){
				System.out.println("bmask not saved");
			}
		}
		Database.removeFile(hash, false);
		System.out.println("Stopping supervisor thread for hash " + hash.toString());
	}

	/**
	 * We have a raw descriptor, we handle it
	 * 
	 * @param d
	 * @return
	 */
	private boolean prepareNewFile(Descriptor d) {
		BitMask b = d.getEmptyBitmask();

		this.file = new File(d, b, path, name);

		if (this.file.reallocate()) {
			currentState = State.CHASING_CHUNKS;
			return true;
		} else {
			this.file = null;
			return false;
		}
	}

	/**
	 * We continue downloading a file.
	 * 
	 * @param d
	 *            Descriptor
	 * @param b
	 *            Bitmask
	 * @return true on success
	 */
	private boolean continueFile(Descriptor d, BitMask b) {
		this.file = new File(d, b, path, name);

		if (!this.file.isAllocated()) {
			if (this.file.reallocate()) {
				currentState = State.CHASING_CHUNKS;
				return true;
			} else {
				this.file = null;
				return false;
			}
		} else {
			currentState = State.CHASING_CHUNKS;
			return true;
		}
	}

	private void slowDown() {
		try {
			sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void lookForDescriptor() {
		Address peer;

		if (!interested.isEmpty()) {
			peer = interested.poll();

			ResponseDescr response = Application.getClient().sendDescrQuery(
					peer, hash, 2000);
			if (response == null) {
				// Returned message is incorrect - we forget about this guy
			} else {
				Descriptor d = response.getDescriptor();
				if (d == null) {
					// Peer does not have this descriptor. But mabye he knows
					// somebody who has it?
					noteworthy.add(peer);
				} else {
					// WE have a descriptor. But is it THE descriptor?

					if (d.getHash().equals(getHash())) {
						if (prepareNewFile(d)) {
							Log.notice("Peer " + peer + " returned a descriptor for " + getFileName());
							interested.add(peer);
							currentState = State.CHASING_CHUNKS;
						} else {
							currentState = State.ERROR;
						}
					} else {
						Log.notice("Received incorrect descriptor");
					}
				}
			}
		} else if (!noteworthy.isEmpty()) {
			
			peer = noteworthy.poll();
			ResponseChase response = Application.getClient().sendChaseQuery(
					peer, hash, 2000);
			if (response == null) {
				// Returned garbage - we forget about this guy
			} else {
				List<Address> peers = response.getPeers();
				System.out.println("returned peer list: " + peers.size());
				int returned = 0;
				for (Address a : peers) {
					System.out.println("PEER ---> " + a);
					if (a != peer && a != Application.getClient().getAddress()) {
						System.out.println("added to noteworthy!");
						noteworthy.add(a);
						returned++;
					}
				}

				if (response.isInterested()) {
					if (returned > 0) {
						Catalogue.addPeer(hash, peer, Catalogue.DEF_TIMEOUT, true);
						interested.add(peer);
					}
				}

			}
		} else if (!checked.isEmpty()) {
			noteworthy.addAll(Catalogue.getRandomPeers(32));
			noteworthy.addAll(checked);
			checked.clear();
		} else {
			slowDown();
		}
	}

	private void lookForChunks() {
		Address peer;
		if (!seeders.isEmpty()) {
			Pair p = seeders.peek();
			ResponseChunk r;
			Chunk c;
			while (p.toCheck < p.bitmask.getSize()) {

				if (p.bitmask.isSet(p.toCheck)) {
					r = Application.getClient().sendChunkQuery(p.peer, hash,
							p.toCheck, file.getDescriptor().getChunkSize(),
							2000);

					if (r != null) {
						c = r.getChunk();
						if (c != null) {
							if (file.setChunk(c, p.toCheck)) {
								Log.notice("Downloaded chunk " + p.toCheck + " of file " + getFileName());
								p.toCheck++;
								break;
							}
						}
					}
					// he returned garbage, let's assume this session is over
					// and we put him into the 'noteworthy' queue
					p.toCheck = p.bitmask.getSize();
					noteworthy.add(p.peer);
					seeders.poll();
					break;
				} else {
					p.toCheck++;
				}
			}
			if (p.toCheck >= p.bitmask.getSize()) {
				seeders.poll();
				interested.add(p.peer);
			}
		} else if (!interested.isEmpty()) {
			peer = interested.poll();
			ResponseBmask r = Application.getClient().sendBmaskQuery(peer,
					hash, file.getBitMask().getSize(), 2000);

			if (r == null) {
				// Forget this peer
			} else {
				BitMask b = r.getBitMask();
				if (b == null) {
					r.print();
					// Forget this peer
				} else {
					Pair p = new Pair();
					if (b.getSize() == file.getBitMask().getSize()) {
						p.bitmask = file.getBitMask().getDiff(b);
						p.peer = peer;
						if (p.bitmask.compareToNull()) {
							checked.add(peer);
						} else {
							Log.notice("Peer " + p.peer + " has " + p.bitmask.getPosessed() + " new chunks.");
							p.toCheck = 0;
							seeders.add(p);
						}
					}
				}
			}
		} else if (!noteworthy.isEmpty()) {
			peer = noteworthy.poll();
			ResponseChase response = Application.getClient().sendChaseQuery(
					peer, hash, 2000);
			if (response == null) {
				// Returned garbage - we forget about this guy
			} else {
				
				List<Address> peers = response.getPeers();
				int returned = 0;
				for (Address a : peers) {
					if (a != peer && a != Application.getClient().getAddress()) {
						noteworthy.add(a);
						returned++;
					}
				}
				if (response.isInterested()) {
					if (returned > 0) {
						Catalogue.addPeer(hash, peer, Catalogue.DEF_TIMEOUT, true);
						interested.add(peer);
					}
				}
			}
		} else if (!checked.isEmpty()) {
			noteworthy.addAll(checked);
			checked.clear();
			noteworthy.addAll(Catalogue.getRandomPeers(32));
		} else {
			slowDown();
		}
	}

	public void seedHash(){
		try {
			System.out.println("seeding");
			sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		initialise();
		Address peer;
		while(currentState != State.ERROR && currentState != State.HALT){
			if(file != null){
				BitMask b = file.getBitMask();
				if(b.isComplete()){
					currentState = State.COMPLETE;
				}
			}
			Application.getUI().showEntry(getHash(), getFileName(), getSize(), getPosessed(), 0, 0, 0);
			while (true) {
				peer = listener.getNext();
				if (peer == null)
					break;
				else {
					System.out.println("Listener returned a potential peer: " + peer);
					noteworthy.add(peer);
				}
			}
			if (currentState == State.LOOKING_FOR_DESCRIPTOR) {
				lookForDescriptor();
			} else 
			if(currentState == State.CHASING_CHUNKS){
				lookForChunks();
			} else 
			if(currentState == State.COMPLETE){
				seedHash();
			}
		}
		finalise();
	}

	public void halt() {
		currentState = State.HALT;
		Application.getUI().dropEntry(getHash());
	}
	
	public int getChunkSize(){
		if(file != null){
			return file.getDescriptor().getChunkSize();
		}else{
			return -1;
		}
	}
	
	public int getChunkCount(){
		if(file != null){
			return file.getDescriptor().getChunkCount();
		}else{
			return -1;
		}
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

	public long getSize() {
		if (file != null) {
			return file.getDescriptor().getFileSize();
		}
		return 0;
	}

	public long getPosessed() {
		if (file != null) {
			BitMask b = file.getBitMask();
			int s = file.getDescriptor().getChunkSize() * b.getPosessed();
			if (b.isSet(b.getSize() - 1)) {
				Descriptor d = file.getDescriptor();
				s -= d.getChunkCount() * d.getChunkSize() - d.getFileSize();
			}
			return s;
		}
		return 0;
	}
}
