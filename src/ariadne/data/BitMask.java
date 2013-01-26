package ariadne.data;

import java.util.Vector;

public class BitMask {
	//TODO
	private byte[] mask;
	public BitMask(byte[] src){
		mask=src.clone();
	}
	public BitMask(){
		
	}
	public boolean isSet(int id){
		//if(mask&&)
		return false;
	}
	public int getSize(){
		return mask.length;
	}
}
