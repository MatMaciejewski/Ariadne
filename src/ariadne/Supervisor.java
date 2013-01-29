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
	private File file;
	private String name;
	private String path;
	private Hash hash;

	public enum State {
		LOOKING_FOR_DESCRIPTOR, CHASING_CHUNKS, COMPLETE, ERROR
	}

	public class Pair {
		public Address peer;
		public BitMask bitmask;
	}

	private State currentState;
	private Queue<Address> noteworthy; // peers worth asking about other peers
	private Queue<Address> interested; // peers that also chase this hash
	private Queue<Address> checked; // peers having a subset of our chunks
	private Queue<Pair> seeders; // peers having something we do not have
	private Catalogue.Listener listener;

	public Supervisor(File file) {
		this.file = file;
		this.name = file.getFileName();
		this.path = file.getFilePath();
		this.hash = file.getDescriptor().getHash();
		currentState = State.CHASING_CHUNKS;
		listener = Catalogue.getListener(getHash());
	}

	public Supervisor(Hash hash, String path, String name) {
		this.name = name;
		this.path = path;
		this.hash = hash;
		currentState = State.LOOKING_FOR_DESCRIPTOR;
		listener = Catalogue.getListener(getHash());
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

	}

	private boolean prepareNewFile(Descriptor d) {
		boolean success = Database.insertFile(d, d.getEmptyBitmask(), path,
				name, true);
		if (success) {
			file = Database.getFile(getHash());
		}
		return success;

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

					Log.notice("Checking if descriptor is correct");

					if (d.getHash().equals(getHash())) {
						System.out.println("so they are the same");
						if (prepareNewFile(d)) {
							interested.add(peer);
							currentState = State.CHASING_CHUNKS;
						} else {
							currentState = State.ERROR;
						}
						System.out.println("and finish");
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

				int returned = 0;
				for (Address a : peers) {
					if (a != peer && a != Application.getClient().getAddress()) {
						noteworthy.add(a);
						returned++;
					}
				}

				if (response.isInterested()) {
					if (returned > 0) {
						checked.add(peer);
					}
				}

			}
		} else if (!checked.isEmpty()) {
			noteworthy.addAll(checked);
			checked.clear();
			noteworthy.addAll(Catalogue.getRandomPeers(32));
		}
	}

	private void lookForChunks() {
		Address peer;
		if (!seeders.isEmpty()) {
			Pair p = seeders.poll();
			
			System.out.println("Asking a seeder " + p.peer.getIpAddress());
			
			ResponseChunk r;
			Chunk c;

			// TODO: RANDOMIZE THIS
			for (int i = 0; i < p.bitmask.getSize(); ++i) {
				if (p.bitmask.get(i)) {
					r = Application.getClient().sendChunkQuery(p.peer, hash, i,
							file.getDescriptor().getChunkSize(), 2000);
					if (r != null) {
						System.out.println("Received a chunk");
						c = r.getChunk();
						
						r.print();
						
						if (c != null) {
							System.out.println("Chunk is not null");
							if (file.setChunk(c, i)) {
								System.out.println("Chunk correct, saved");
								continue;
							}
						}
					}
				}
				checked.add(p.peer);
				break;
			}
		} else if (!interested.isEmpty()) {
			peer = interested.poll();
			ResponseBmask r = Application.getClient().sendBmaskQuery(peer, hash, file.getBitMask().getSize(), 2000);

			System.out.println("Bmask response analysis:");

			if (r == null) {
				System.out.println("no bitmask. Fail");
				// Forget this peer
			} else {
				BitMask b = r.getBitMask();

				if (b == null) {

					r.print();

					System.out.println("no bitmask after all");
					// Forget this peer
				} else {
					System.out.println("bitmask correct");
					Pair p = new Pair();

					System.out
							.println("received bitmask size:  " + b.getSize());
					System.out.println("our local bitmask size: "
							+ file.getBitMask().getSize());

					if (b.getSize() == file.getBitMask().getSize()) {
						p.bitmask = file.getBitMask().getDiff(b);
						p.peer = peer;
						if (p.bitmask.compareToNull()) {
							
							System.out.println("diff is empty");
							checked.add(peer);
						} else {
							System.out.println("dif not empty - download!");
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
						interested.add(peer);
					}
				}
			}
		} else if (!checked.isEmpty()) {
			noteworthy.addAll(checked);
			checked.clear();
			noteworthy.addAll(Catalogue.getRandomPeers(32));
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
				else {
					interested.add(peer);
					System.out.println("added " + peer.getIpAddress());
				}
			}

			if (currentState == State.LOOKING_FOR_DESCRIPTOR) {
				System.out.println("Looking for descriptor");
				lookForDescriptor();
			} else {
				System.out.println("Looking for chunks");
				lookForChunks();
			}

			Application.getUI().showEntry(getHash(), getFileName(), getSize(),
					getPosessed(), 0, 0, 0);

			try {
				System.out.println("sleep");
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
		Application.getUI().dropEntry(getHash());
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
			if (b.get(b.getSize() - 1)) {
				Descriptor d = file.getDescriptor();
				s -= d.getChunkCount() * d.getChunkSize() - d.getFileSize();
			}
			return s;
		}
		return 0;
	}
}
