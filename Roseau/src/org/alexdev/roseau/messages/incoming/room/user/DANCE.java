package org.alexdev.roseau.messages.incoming.room.user;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class DANCE implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		if (player.getRoomEntity().getRoom() == null) {
			return;
		}
		
		player.getRoomEntity().setStatus("dance", "");
		player.getRoomEntity().setNeedUpdate(true);
	}

}
