package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class LOGOUT implements OutgoingMessageComposer {

	private String username;

	public LOGOUT(String username) {
		this.username = username;
	}

	@Override
	public void write(Response response) {
		response.init("LOGOUT");
		response.appendNewArgument(this.username);
	}

}
