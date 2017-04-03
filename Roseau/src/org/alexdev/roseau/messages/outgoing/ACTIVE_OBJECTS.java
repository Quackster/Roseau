package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class ACTIVE_OBJECTS implements OutgoingMessageComposer {

	private Room room;

	public ACTIVE_OBJECTS(Room room) {
		this.room = room;
	}

	@Override
	public void write(Response response) {
		response.init("ITEMS");// " + room.getData().getModelName());

		for (Item item : this.room.getItems()) {
			if (!item.getDefinition().getBehaviour().isPassiveObject()) {
				response.appendObject(item);
			}
		}
	}

}
