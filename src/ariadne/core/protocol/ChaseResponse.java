package ariadne.core.protocol;

public class ChaseResponse extends Message {
	
	public ChaseResponse(){
		super();
	}

	@Override
	public Type getType() {
		return Message.Type.CHASE_RESPONSE;
	}

	@Override
	public boolean isComplete() throws InvalidMessageException {
		return false;
	}

}
