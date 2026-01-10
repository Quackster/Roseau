package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;

public class DOORFLAT extends OutgoingMessageComposer {
	private int itemId;
	private int roomId;

	public DOORFLAT(int itemId, int roomId) {
		this.itemId = itemId;
		this.roomId = roomId;
	}

	@Override
	public void write() {
		response.init("DOORFLAT");
		response.appendNewArgument(String.valueOf(this.itemId));
		response.appendNewArgument(String.valueOf(this.roomId));
	}
}
