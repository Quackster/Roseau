package org.alexdev.roseau.messages.incoming.login;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class INITUNITUSER implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

	}
}
