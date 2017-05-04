package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class SYSTEMBROADCAST extends OutgoingMessageComposer {

	private String message;

	public SYSTEMBROADCAST(String message) {
		this.message = message;
	}

	@Override
	public void write() {
		response.init("SYSTEMBROADCAST");
		response.appendNewArgument(this.message);
	}

}
