package org.alexdev.roseau.messages.incoming.room;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class GOAWAY implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
	
		player.dispose();
		player.getNetwork().close();
	}

}
