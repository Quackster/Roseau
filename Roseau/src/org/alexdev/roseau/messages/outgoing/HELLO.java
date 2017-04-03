package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
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
