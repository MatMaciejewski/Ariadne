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
	private boolean finished;

	public enum State {
		LOOKING_FOR_DESCRIPTOR, CHASING_CHUNKS, COMPLETE
	}

	private State currentState;
	private Queue<Address> toAsk;
	private Queue<Address> interested;
	private Queue<Address> iHaveStuff;

	public Supervisor(Hash hash, String path, String name) {
		this.name = name;
		this.path = path;
		this.hash = hash;
		finished = false;
		iHaveStuff = new LinkedList<Address>();
		interested = new LinkedList<Address>();
		toAsk = new LinkedList<Address>();
	}

	public void initialise() {
		try {
			Descriptor d = Descriptor.parseFile(path + name + ".desc");
			currentState = State.CHASING_CHUNKS;
		} catch (Exception e) {
			currentState = State.LOOKING_FOR_DESCRIPTOR;
		}

		toAsk.addAll(Catalogue.getPeerForHash(hash, Integer.MAX_VALUE));
	}

	public void finalise() {

	}

	public void loop() {
		Catalogue.Listener l = Catalogue.getListener(getHash());
		Address wsad = l.getNext();
		if (wsad != null) {
			interested.add(wsad);
		}
		System.out.println("Loop");
		// System.out.println("Downloading hash " + hash);
		// if (Catalogue.getPeerNumber() < 5 && iHaveStuff.isEmpty()
		// && interested.isEmpty()) {
		// System.out.println("NOPE1");
		// } else
		if (!iHaveStuff.isEmpty() && currentState == State.CHASING_CHUNKS) {
			System.out.println("NOPE2");
			Address userAddress = iHaveStuff.poll();
			Client user = new Client(userAddress);
			ResponseBmask mask = null;
			int lostChunk = -1;
			int ChunkSize = -1;
			Response resp = user.sendBmaskQuery(userAddress, hash, 2000);
			if (resp != null) {
				mask = (ResponseBmask) resp;
			}
			if (mask != null) {
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
					ResponseChunk fileChunk = (ResponseChunk) user
							.sendChunkQuery(userAddress, hash, lostChunk,
									Database.getFile(hash).getDescriptor()
											.getChunkSize(), 2000);
					Chunk imported = fileChunk.getChunk();
					if (Database.getFile(hash) != null)
						Database.getFile(hash).setChunk(imported, lostChunk);
					iHaveStuff.add(userAddress);
				} else
					iHaveStuff.add(userAddress);
			}
			else Log.notice("Server did not respond.");
		} else if (!interested.isEmpty()
				&& currentState == State.CHASING_CHUNKS) {
			System.out.println("NOPE3");
			Address userAddress = interested.poll();
			Client user = new Client(userAddress);
			ResponseBmask back = null;
			Response resp = user.sendBmaskQuery(userAddress, hash, 2000);
			if (resp != null) {
				back = (ResponseBmask) resp;
			}
			if (back != null) {
				if (back.hasBitMask())
					iHaveStuff.add(userAddress);
				else
					toAsk.add(userAddress);
			}
			else Log.notice("Server did not respond.");
		} else if (!toAsk.isEmpty() && currentState == State.CHASING_CHUNKS) {
			System.out.println("NOPE4");
			Address userAddress = toAsk.poll();
			Client user = new Client(userAddress);
			ResponseChase back=null;
			Response resp = user.sendChaseQuery(
					userAddress, hash, 2000);
			if (resp != null) {
				back = (ResponseChase) resp;
			}
			if (back != null) {
				List<Address> potential = back.getPeers();
				if (potential.contains(userAddress)) {
					interested.add(userAddress);
					potential.remove(userAddress);
					toAsk.addAll(potential);
				}
			}
			else Log.notice("Server did not respond.");
		} else if (!toAsk.isEmpty()
				&& currentState == State.LOOKING_FOR_DESCRIPTOR) {
			System.out.println("NOPE5");
			Address userAddress = toAsk.poll();
			Client user = new Client(userAddress);
			ResponseDescr back=null;
			Response resp = user.sendDescrQuery(
					userAddress, hash, 2000);
			if (resp != null) {
				back = (ResponseDescr) resp;
			}
			if (back != null) {
				Descriptor d = back.getDescriptor();
				if (d != null) {
					currentState = State.CHASING_CHUNKS;

					interested.add(userAddress);
				}
			}
			else Log.notice("Server did not respond.");
		}
		/*
		 * else if (!toAsk.isEmpty()) { System.out.println("NOPE6"); Address
		 * userAddress = toAsk.peek(); Client user = new Client(userAddress);
		 * ResponsePeers back = (ResponsePeers) user.sendPeersQuery(
		 * userAddress, 2000); List<Address> potential = back.getPeers(); if
		 * (potential.contains(userAddress)) { potential.remove(userAddress);
		 * toAsk.addAll(potential); } }
		 */
		else {
			System.out.println("Else 7");
			List<Address> newPeers = Catalogue.getRandomPeers();
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
		try {
			initialise();
			while (currentState != State.COMPLETE) {
				loop();
			}
		} catch (Exception e) {
			System.out
					.println("CRITICAL ERROR IN Supervisor -------------------------------");
			e.printStackTrace();
			System.out.println(e.getMessage());
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
