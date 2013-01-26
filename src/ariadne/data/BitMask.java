package ariadne.data;

public class BitMask {
	// TODO
	private byte[] mask;

	public BitMask(byte[] src) {
		mask = src.clone();
	}

	public BitMask() {

	}

	public boolean isSet(int id) {
		int target = id/8;
		int offset = id%8;
		// mask&target>>offset;
		// if(mask&&)
		return false;
	}

	public int getSize() {
		return mask.length;
	}
}
