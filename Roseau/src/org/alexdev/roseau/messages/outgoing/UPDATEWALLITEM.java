package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class UPDATEWALLITEM implements OutgoingMessageComposer {

	private Item item;

	public UPDATEWALLITEM(Item item) {
		this.item = item;
	}

	@Override
	public void write(Response response) {
		response.init("UPDATEITEM");
		response.appendObject(this.item);
	}

}
