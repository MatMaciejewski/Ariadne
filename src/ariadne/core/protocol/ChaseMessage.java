package ariadne.core.protocol;

import ariadne.core.Hash;

public class ChaseMessage extends Message {

	public ChaseMessage() {
		super();

	}
	
	public ChaseMessage(Hash hash){
		super();
		data.add(Message.Type.CHASE_MESSAGE.code);
		
		byte[] raw = hash.getBytes();
		for(int i=0;i<Hash.LENGTH;++i){
			data.add(raw[i]);
		}
	}

	@Override
	public Type getType() {
		return Message.Type.CHASE_MESSAGE;
	}

	@Override
	public boolean isComplete() throws InvalidMessageException {
		int size = data.size();
		if (size == 0)
			return false;
		if (data.get(0) != getType().code)
			throw new InvalidMessageException();

		if (size < 1+Hash.LENGTH)
			return false;
		else if (size == 1+Hash.LENGTH)
			return true;
		else
			throw new InvalidMessageException();
	}
	
	public Hash getHash(){
		return new Hash(data.toArray(new Byte[Hash.LENGTH]), 1);
	}
}
