package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class DOORFLAT implements OutgoingMessageComposer {
	
	private int itemID;
	private int roomID;

	public DOORFLAT(int itemID, int roomID) {
		this.itemID = itemID;
		this.roomID = roomID;
	}

	@Override
	public void write(Response response) {

		response.init("DOORFLAT");
		response.appendNewArgument(String.valueOf(this.itemID));
		response.appendNewArgument(String.valueOf(this.roomID));
	}
}
