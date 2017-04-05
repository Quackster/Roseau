package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.MESSENGER_MSG;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MESSENGER_SENDMSG implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		player.send(new MESSENGER_MSG(player));
	}

}
