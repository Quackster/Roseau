package org.alexdev.roseau.messages.incoming.handshake;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.messages.outgoing.handshake.LOGINOK;
import org.alexdev.roseau.messages.outgoing.handshake.RIGHTS;
import org.alexdev.roseau.messages.outgoing.handshake.SECRETKEY;
import org.alexdev.roseau.server.messages.ClientMessage;

public class TRY_LOGIN implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		player.send(new RIGHTS());
		player.send(new LOGINOK());

	}

}
