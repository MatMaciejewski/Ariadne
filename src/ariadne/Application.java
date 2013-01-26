package ariadne;

import java.util.Set;

import ariadne.data.Hash;
import ariadne.utils.TimedMultiMap;

public class Application{
	
	
	
	public static void main(String[] args){
		TimedMultiMap<Hash, Double> m = new TimedMultiMap<Hash, Double>();
		
		Hash a = Hash.computeFromString("asd");
		Hash b = Hash.computeFromString("qwe");
		Hash c = Hash.computeFromString("rty");
		
		System.out.println("asd: "+a);
		System.out.println("qwe: "+b);
		System.out.println("rty: "+c);
		
		m.add(a, new Double(21), 5);
		m.add(a, new Double(16), 9);
		m.add(c, new Double(10), 7);
		
		System.out.println("size: " + m.size());
		
		m.removeTimeouted(6);
		
		Set<Double> content = m.get(a, 5);
		
		System.out.println("content: " + content.size());
		
		for(Double v: content){
			System.out.println(v);
		}	
	}
}