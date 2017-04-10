package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class ACTIVEOBJECT_REMOVE implements OutgoingMessageComposer {

	private String itemID;

	public ACTIVEOBJECT_REMOVE(String packetID) {
		this.itemID = packetID;
	}

	@Override
	public void write(Response response) {
		response.init("ACTIVEOBJECT_REMOVE");
		response.appendNewArgument(this.itemID);
	}
}
