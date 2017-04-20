package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class ITEMS implements OutgoingMessageComposer {

	private Item item;

	public ITEMS(Item item) {
		this.item = item;
	}

	@Override
	public void write(Response response) {
		response.init("ITEMS");
		
		if (item.getDefinition().getBehaviour().isOnWall()) {
			response.appendObject(item);
		}
	}
}
