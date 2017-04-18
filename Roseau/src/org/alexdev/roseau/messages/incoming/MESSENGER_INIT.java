package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.BUDDYLIST;
import org.alexdev.roseau.messages.outgoing.MESSENGERREADY;
import org.alexdev.roseau.messages.outgoing.MESSENGER_MSG;
import org.alexdev.roseau.messages.outgoing.MYPERSISTENTMSG;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MESSENGER_INIT implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		player.send(new MYPERSISTENTMSG("message placeholder"));
		player.send(new BUDDYLIST());
		//player.send(new BUDDYADDREQUESTS());
		player.send(new MESSENGERREADY());

		player.send(new MESSENGER_MSG(player, Roseau.getUtilities().getTimestamp(), "Welcome to FUSE Hotel\n\nThis is one of the most complete V1 servers right now.\n\nAdministrator and staff is Alex.\n\nYou get 25 credits every 10 minutes."));
	}

}
