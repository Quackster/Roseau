package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.server.messages.ClientMessage;

public interface MessageEvent {
	public void handle(Player player, ClientMessage reader);
}
