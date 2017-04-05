package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class ACTIVE_OBJECTS implements OutgoingMessageComposer {

	private Room room;

	public ACTIVE_OBJECTS(Room room) {
		this.room = room;
	}

	@Override
	public void write(Response response) {
		response.init("ACTIVE_OBJECTS");

		if (this.room != null) {
			
			if (this.room.getData().getRoomType() == RoomType.PUBLIC) {
				return;
			}
			
			for (Item item : this.room.getItems()) {
				response.appendObject(item);
			}
		}
	}
}
