package ariadne.data;

import java.util.HashSet;
import java.util.Random;

import ariadne.net.Address;
import ariadne.utils.TimedMultiMap;

public class Catalogue {
	private TimedMultiMap<Hash,Address> peers;
	private static final int PEERS_NUMBER = 10;
	private static final int RANDOM_PEERS = 10;
	
	public HashSet<Address> getPeerForHash(Hash hash) {
		HashSet<Address> listOfPeers = new HashSet<Address>();
		if (peers.get(hash,PEERS_NUMBER)!=null)
			listOfPeers = peers.get(hash,PEERS_NUMBER);
		return listOfPeers;
	}

	public HashSet<Address> getRandomPeers() {
		HashSet<Address> listOfRandomPeers = new HashSet<Address>();
		Random generator = new Random();
		Object[] values = peers.getValues();
		for (int i = 0; i < RANDOM_PEERS; i++) {
			Object peer = values[generator.nextInt(values.length)];
			if (!listOfRandomPeers.contains(peer)) {
				listOfRandomPeers.add((Address) peer);
			}
		}
		return listOfRandomPeers;
	}
	
	public void addPeer(Hash hash, Address peer, int timeout){
		peers.add(hash,peer,timeout);
	}
	
}
