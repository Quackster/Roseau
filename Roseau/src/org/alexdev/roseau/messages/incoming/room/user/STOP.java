package org.alexdev.roseau.messages.incoming.room.user;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class STOP implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		if (player.getRoomEntity().getRoom() == null) {
			return;
		}
		
		if (reader.getArgumentAmount() < 1) {
			return;
		}
		
		String stopWhat = reader.getArgument(0);
		
		if (stopWhat.equals("Dance")) {
			player.getRoomEntity().removeStatus("dance");
			player.getRoomEntity().setNeedUpdate(true);
		}
	}

}
