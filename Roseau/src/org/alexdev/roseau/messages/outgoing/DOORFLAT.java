package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class DOORFLAT implements OutgoingMessageComposer {
	
	private int itemId;
	private int roomId;

	public DOORFLAT(int itemId, int roomId) {
		this.itemId = itemId;
		this.roomId = roomId;
	}

	@Override
	public void write(Response response) {

		response.init("DOORFLAT");
		response.appendNewArgument(String.valueOf(this.itemId));
		response.appendNewArgument(String.valueOf(this.roomId));
	}
}
