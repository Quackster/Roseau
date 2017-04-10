
package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class FLATCREATED implements OutgoingMessageComposer {

	private Room room;

	public FLATCREATED(Room room) {
		this.room = room;
	}

	@Override
	public void write(Response response) {
		response.init("FLATCREATED");
		response.appendNewArgument(Integer.toString(room.getData().getID()));
		response.appendArgument(room.getData().getName());

	}

}
