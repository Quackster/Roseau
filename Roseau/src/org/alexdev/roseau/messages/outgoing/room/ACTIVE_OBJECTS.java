package org.alexdev.roseau.messages.outgoing.room;

import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class ACTIVE_OBJECTS implements OutgoingMessageComposer {

	@Override
	public void write(Response response) {
		response.init("ACTIVE_OBJECTS 0");
	}

}
