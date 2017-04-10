package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class FLATINFO implements OutgoingMessageComposer {

	private Room room;

	public FLATINFO(Room room) {
		this.room = room;
	}

	@Override
	public void write(Response response) {
		response.init("SETFLATINFO");
		response.append("/");
		response.append(String.valueOf(this.room.getData().getID()));
		response.append("/");
	}

}
