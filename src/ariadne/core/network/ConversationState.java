package ariadne.core.network;

import ariadne.core.protocol.MessageParser;

public class ConversationState{
	public MessageParser parser;
	
	public ConversationState(){
		parser = new MessageParser();
	}
}
