package org.alexdev.roseau.messages.outgoing.room.entry;

import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class ROOM_READY implements OutgoingMessageComposer {

	private String description;

	public ROOM_READY(String description) {
		this.description = description;
	}

	@Override
	public void write(Response response) {
		response.init("ROOM_READY");
		response.appendNewArgument(this.description);
	}

}
