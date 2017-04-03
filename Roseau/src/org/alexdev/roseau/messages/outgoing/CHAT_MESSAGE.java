package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class CHAT_MESSAGE implements OutgoingMessageComposer {

	private String header;
	private String talkMessage;
	private String username;

	public CHAT_MESSAGE(String header,  String username, String talkMessage) {
		this.header = header;
		this.username = username;
		this.talkMessage = talkMessage;
	}

	@Override
	public void write(Response response) {

		response.init(this.header);
		response.appendNewArgument(this.username);
		response.appendArgument(this.talkMessage);
	}

}
