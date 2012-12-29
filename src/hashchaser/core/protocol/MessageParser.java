package hashchaser.core.protocol;

public class MessageParser {
	private boolean invalidated = false;
	private Message msg = null;

	/**
	 * Adds received data to the message.
	 * @param data
	 * @param limit
	 */
	public void addData(byte[] data, int limit) {
		if(invalidated) return;
		if (data.length > 0) {
			if (msg == null) {
				for (Message.Type type : Message.Type.values()) {
					if (type.code == data[0]) {
						try {
							msg = type.className.newInstance();
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						break;
					}
				}
				if (msg == null){
					invalidated = true;
					return;
				}
			}
			msg.addData(data, limit);
		}
	}

	/**
	 * Returns the message if it is ready, null otherwise. 
	 * @return Message
	 * @throws Message.InvalidMessageException Thrown if added data make no sense.
	 */
	public Message getMessage() throws Message.InvalidMessageException{
		if (invalidated) throw new Message.InvalidMessageException();
		if (msg.isComplete())
			return msg;
		return null;
	}
}
