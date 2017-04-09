package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class ACTIVEOBJECT_ADD implements OutgoingMessageComposer {

	private Item item;

	public ACTIVEOBJECT_ADD(Item item) {
		this.item = item;
	}

	@Override
	public void write(Response response) {
		response.init("ACTIVEOBJECT_ADD");
		response.appendObject(this.item);
	}

}
