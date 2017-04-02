package org.alexdev.roseau.messages.incoming.room;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.messages.outgoing.room.FLAT_LETIN;
import org.alexdev.roseau.server.messages.ClientMessage;

public class TRYFLAT implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		int id = Integer.valueOf(reader.getArgument(1, "/"));

		Room room = Roseau.getGame().getRoomManager().getRoomById(id);

		if (room != null) {
			player.getRoomUser().setRoom(room);
			player.send(new FLAT_LETIN());
		}
	}
}
