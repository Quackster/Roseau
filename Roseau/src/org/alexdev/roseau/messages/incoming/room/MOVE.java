package org.alexdev.roseau.messages.incoming.room;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MOVE implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		if (reader.getArgumentAmount() < 2) {
			return;
		}
		
		int x = Integer.valueOf(reader.getArgument(0));
		int y = Integer.valueOf(reader.getArgument(1));
		
		if (x == 11 && y == 33) {
			
			Room room = Roseau.getGame().getRoomManager().getRoomByPort(120);
			room.loadRoom(player);
		}
	}

}
