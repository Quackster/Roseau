package org.alexdev.roseau.messages.outgoing.room;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class OBJECTS_WORLD implements OutgoingMessageComposer {

	private Room room;
	
	public OBJECTS_WORLD(Room room) {
		this.room = room;
	}

	@Override
	public void write(Response response) {
		response.init(" OBJECTS WORLD 0 " + room.getData().getModelName());
		
		for (Item item : this.room.getItems()) {
			response.appendObject(item);
		}
	}

}
