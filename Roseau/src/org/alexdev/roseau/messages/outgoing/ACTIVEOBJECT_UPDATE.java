package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class ACTIVEOBJECT_UPDATE implements OutgoingMessageComposer {

	private Item item;

	public ACTIVEOBJECT_UPDATE(Item item) {
		this.item = item;
	}

	@Override
	public void write(Response response) {
		response.init("ACTIVEOBJECT_UPDATE");

		if (this.item != null) {
			response.appendObject(item);
		}
	}
}
