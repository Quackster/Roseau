package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.BUDDYADDREQUESTS;
import org.alexdev.roseau.messages.outgoing.BUDDYLIST;
import org.alexdev.roseau.messages.outgoing.MESSENGERREADY;
import org.alexdev.roseau.messages.outgoing.MYPERSISTENTMSG;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MESSENGER_INIT implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		player.send(new MYPERSISTENTMSG("message placeholder"));
		player.send(new BUDDYLIST());
		player.send(new BUDDYADDREQUESTS());
		player.send(new MESSENGERREADY());
	
		
	}

}
