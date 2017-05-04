package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class HELLO extends OutgoingMessageComposer {
	
	public HELLO() {
		super();
	}

	@Override
	public void write() {
		response.init("HELLO");
	}
}
