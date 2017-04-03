package org.alexdev.roseau.messages.outgoing;

import java.util.List;

import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class BUSY_FLAT_RESULTS implements OutgoingMessageComposer {

	public List<Room> rooms;
	
	public BUSY_FLAT_RESULTS(List<Room> rooms) {
		this.rooms = rooms;
	}

	@Override
	public void write(Response response) {
		response.init("BUSY_FLAT_RESULTS 1");
		
		for (Room room : this.rooms) {
			response.appendObject(room);
		}
	}

}
