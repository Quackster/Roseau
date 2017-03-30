package org.alexdev.roseau.messages.outgoing.messenger;

import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class MESSENGERSREADY implements OutgoingMessageComposer {

	@Override
	public void write(Response response) {
		response.init("MESSENGERSREADY");
	}

}
