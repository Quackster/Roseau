package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class ERROR implements OutgoingMessageComposer {

	private String errorMessage;
	
	public ERROR(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public void write(Response response) {
		response.init("ERROR");
		response.appendArgument(this.errorMessage);
	}

}
