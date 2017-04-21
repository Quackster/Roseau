package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class ADDWALLITEM implements OutgoingMessageComposer {

	private Item item;

	public ADDWALLITEM(Item item) {
		this.item = item;
	}

	@Override
	public void write(Response response) {
		response.init("ADDITEM");
		response.appendObject(this.item);
	}

}
