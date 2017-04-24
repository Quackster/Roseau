package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class DOORBELL_RINGING implements OutgoingMessageComposer {

	private String name;
	
	public DOORBELL_RINGING(String name) {
		this.name = name;
	}

	@Override
	public void write(Response response) {
		response.init("DOORBELL_RINGING");
		response.appendNewArgument(this.name);
	}

}
