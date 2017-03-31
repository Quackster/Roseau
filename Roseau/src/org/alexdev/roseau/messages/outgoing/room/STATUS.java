package org.alexdev.roseau.messages.outgoing.room;

import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class STATUS implements OutgoingMessageComposer {

	private Room room;

	public STATUS(Room room) {
		this.room = room;
	}

	@Override
	public void write(Response response) {
		response.init("STATUS " + (char)13 + "Alex" + " " + room.getData().getModel().getDoorX() + "," + room.getData().getModel().getDoorY() + "," + room.getData().getModel().getDoorZ() + ",0,0/mod 0/");
	}

}
