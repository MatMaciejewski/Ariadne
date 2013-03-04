package ariadne;


import ariadne.data.Address;
import ariadne.data.Hash;
import ariadne.db.Catalogue;


public class Ariadne {
	public static void main(String[] args){	
		
		Catalogue c = new Catalogue();
		
		Hash h = Hash.calculatedFromString("asd");
		Address a = Address.fromHostName("c0a8:0101::5", 25566);
		c.add(h, a, 500);
		
		
		Debug.printHex(h.getBuffer());
		
		for(Address tmp: c.getAddresses(Hash.calculatedFromString("asd"))){
			Debug.printUnsigned(tmp.getBuffer());
		}
		
	}
}
