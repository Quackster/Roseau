package org.alexdev.roseau.messages.outgoing.login;

import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class SYSTEMBROADCAST implements OutgoingMessageComposer {

	private String message;

	public SYSTEMBROADCAST(String message) {
		this.message = message;
	}

	@Override
	public void write(Response response) {
		response.init("SYSTEMBROADCAST");
		response.appendNewArgument(this.message);
	}

}
