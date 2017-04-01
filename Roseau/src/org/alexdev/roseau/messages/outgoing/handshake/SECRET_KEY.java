package org.alexdev.roseau.messages.outgoing.handshake;

import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class SECRET_KEY implements OutgoingMessageComposer {

	private String key;
	
	public SECRET_KEY(String key) {
		super();
		this.key = key;
	}

	@Override
	public void write(Response response) {
		response.init("SECRET_KEY");
		response.appendNewArgument(key);
	}
}
