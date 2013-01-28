package ariadne;

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
import ariadne.protocol.ResponseBmask;
import ariadne.protocol.ResponseChase;
import ariadne.protocol.ResponseChunk;

public class Supervisor extends Thread {
	private String name;
	private String path;
	private Hash hash;
	private boolean finished;

	public enum State {
		LOOKING_FOR_DESCRIPTOR, CHASING_CHUNKS, COMPLETE
	}

	private State currentState;
	private Queue<Address> toAsk;
	private Queue<Address> interested;
	private PriorityQueue<Address> iHaveStuff;

	public Supervisor(Hash hash, String path, String name) {
		this.name = name;
		this.path = path;
		this.hash = hash;
		finished = false;
	}

	public void initialise() {
		try {
			Descriptor d = Descriptor.parseFile(path + name + ".desc");
			currentState = State.CHASING_CHUNKS;
		} catch (Exception e) {
			currentState = State.LOOKING_FOR_DESCRIPTOR;
		}
	}

	public void finalise() {

	}

	public void loop() {
		// System.out.println("Downloading hash " + hash);
		if (!iHaveStuff.isEmpty() && currentState == State.CHASING_CHUNKS) {
			Address userAddress = iHaveStuff.poll();
			Client user = new Client(userAddress);
			int lostChunk = -1;
			int ChunkSize = -1;
			ResponseBmask mask = (ResponseBmask) user.sendBmaskQuery(
					userAddress, hash, 2000);
			BitMask comparator = mask.getBitMask();
			BitMask difference = Database.getFile(hash).getBitMask()
					.getDiff(comparator);
			if (!difference.compareToNull()) {
				for (int i = 0; i < comparator.getSize(); i++) {
					if (difference.get(i) == true) {
						lostChunk = i;
						break;
					}
				}
				ResponseChunk fileChunk = (ResponseChunk) user.sendChunkQuery(
						userAddress, hash, lostChunk, Database.getFile(hash).getDescriptor().getChunkSize(), 2000);
				Chunk imported = fileChunk.getChunk();
				if (Database.getFile(hash) != null)
					Database.getFile(hash).setChunk(imported, lostChunk);
				iHaveStuff.add(userAddress);
			} else
				iHaveStuff.add(userAddress);
		} else if (!interested.isEmpty()&&currentState == State.CHASING_CHUNKS) {
			Address userAddress = interested.poll();
			Client user = new Client(userAddress);
			ResponseBmask ret = (ResponseBmask) user.sendBmaskQuery(
					userAddress, hash, 2000);
			if (ret.hasBitMask())
				iHaveStuff.add(userAddress);
			else
				iHaveStuff.add(userAddress); // No duplication here, keep
												// looking ->Guys that don't
												// have anything added to
												// iHaveStuff -> FAIL
		} else if (!toAsk.isEmpty()) {
			Address askPeer = toAsk.poll();
			Client peer = new Client(askPeer);
			ResponseChase back = (ResponseChase) peer.sendChaseQuery(
					askPeer, hash, 2000);
			List<Address> potential = back.getPeers();
			if (potential.contains(askPeer)) {
				interested.add(askPeer);
				potential.remove(askPeer);
				toAsk.addAll(potential);
			}
		} else{
			Set<Address> newPeers = Catalogue.getRandomPeers();
			toAsk.addAll(newPeers);
		}
		try {
			sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("sleep interrupted");
		}

	}

	@Override
	public void run() {
		initialise();
		while (currentState != State.COMPLETE) {
			loop();
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
