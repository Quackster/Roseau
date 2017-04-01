package org.alexdev.roseau.messages.outgoing.handshake;

import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class HELLO implements OutgoingMessageComposer {
	
	public HELLO() {
		super();
	}

	@Override
	public void write(Response response) {
		response.init("HELLO");
	}
}
