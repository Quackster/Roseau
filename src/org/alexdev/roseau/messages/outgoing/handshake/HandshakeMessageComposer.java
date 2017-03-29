package org.alexdev.roseau.messages.outgoing.handshake;

import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class HandshakeMessageComposer implements OutgoingMessageComposer {

	@Override
	public void write(Response response) {
		response.init(0);
		//response.appendString("testing123");
	}
}
