package ariadne.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import ariadne.net.Address;

public class Catalogue {
	private HashMap<Hash, HashSet<Address>> peers;
	private static final int RANDOM_PEERS = 10;
	
	public HashSet<Address> getPeerForHash(Hash hash) {
		HashSet<Address> listOfPeers = new HashSet<Address>();
		if (peers.containsKey(hash))
			listOfPeers = peers.get(hash);
		return listOfPeers;
	}

	public HashSet<Address> getRandomPeers() {
		HashSet<Address> listOfRandomPeers = new HashSet<Address>();
		Random generator = new Random();
		Object[] values = peers.values().toArray();
		for (int i = 0; i < RANDOM_PEERS; i++) {
			Object peer = values[generator.nextInt(values.length)];
			if (!listOfRandomPeers.contains(peer)) {
				listOfRandomPeers.add((Address) peer);
			}
		}
		return listOfRandomPeers;
	}
	
	public void addPeer(Hash hash, Address peer){
		HashSet<Address> currentPeers = new HashSet<Address>();
		if(peers.get(hash)!=null){
			 currentPeers = peers.get(hash);
		}
		currentPeers.add(peer);
		peers.put(hash, currentPeers);
	}
	
}
