package ariadne.protocol;

import ariadne.data.Hash;

public class ResponseBmask extends Response{

	@Override
	public byte[] getContent() {
		
		// TODO Auto-generated method stub
		return null;
	}
	public ResponseBmask(byte[] cont){
		
	}
	
	@Override
	public boolean isComplete() throws InvalidMessageException {
		byte[] bytes = new byte[1];
		int length = getByteBuffer().get(bytes, 0, bytes.length).getInt();
		if(getByteBuffer().limit()<length+1) return false;
		else return true;
	}

}
