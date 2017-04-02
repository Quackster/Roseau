package org.alexdev.roseau.messages.incoming.login;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.messages.outgoing.user.USEROBJECT;
import org.alexdev.roseau.server.messages.ClientMessage;

public class INFORETRIEVE implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		if (player.getDetails().isAuthenticated()) {
			player.send(new USEROBJECT(player.getDetails()));
		}
	}
}
