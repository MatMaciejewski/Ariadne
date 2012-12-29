package hashchaser.core.network;

import hashchaser.core.protocol.MessageParser;

public class ConversationState{
	public MessageParser parser;
	
	public ConversationState(){
		parser = new MessageParser();
	}
}
