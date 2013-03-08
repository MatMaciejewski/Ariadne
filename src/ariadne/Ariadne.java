package ariadne;

import java.util.ArrayList;

import ariadne.data.FileDescriptor;
import ariadne.data.Hash;
import ariadne.db.ConcreteDatabase;
import ariadne.db.Database;


public class Ariadne {
	public static void main(String[] args){	
		Database d = new ConcreteDatabase();
		
		ArrayList<Hash> hashes = new ArrayList<Hash>();
		hashes.add(Hash.calculatedFromString("asd"));
		hashes.add(Hash.calculatedFromString("qwe"));
		hashes.add(Hash.calculatedFromString("dfg"));
		
		FileDescriptor desc = FileDescriptor.fromData(8192, 24000, hashes);
		
		Debug.printHex(desc);
	}
}
