package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.settings.RoomState;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.ERROR;
import org.alexdev.roseau.messages.outgoing.FLAT_LETIN;
import org.alexdev.roseau.server.messages.ClientMessage;

public class TRYFLAT implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		int id = Integer.valueOf(reader.getArgument(1, "/"));
		String password = reader.getArgument(2, "/");

		Room room = Roseau.getGame().getRoomManager().getRoomById(id);

		if (room != null) {
			
			if (room.getData().getState() == RoomState.PASSWORD) {
				if (!password.equals(room.getData().getPassword())) {
					player.send(new ERROR("Incorrect flat password"));
					return;
				}
			}
			
			player.getRoomUser().setRoom(room);
			player.send(new FLAT_LETIN());
		}
	}
}
