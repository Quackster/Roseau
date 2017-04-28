package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.MESSENGERREADY;
import org.alexdev.roseau.messages.outgoing.MYPERSISTENTMSG;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MESSENGER_INIT implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		if (!player.getDetails().isAuthenticated()) {
			return;
		}
		
		if (player.getNetwork().getServerPort() != Roseau.getServerPort()) {
			return; // Only load messenger on main serve connection
		}

		
		player.send(new MYPERSISTENTMSG("message placeholder"));
	
		player.getMessenger().load();
		player.getMessenger().sendStatus();
		
		player.getMessenger().sendRequests();
		player.getMessenger().sendFriends();
		
		//player.send(new BUDDYLIST());
		//player.send(new BUDDYADDREQUESTS());
		
		player.send(new MESSENGERREADY());
		player.getMessenger().setInitalised(true);

		/*if (player.canSendHotelAlert()) {
			player.send(new MESSENGER_MSG(player, Roseau.getUtilities().getTimestamp(), "Welcome to FUSE Hotel\n\nThis is one of the most complete V1 servers right now.\n\nAdministrator and staff is Alex.\n\nYou get 10 credits every 10 minutes."));
			player.setSendHotelAlert(false);
		}*/
	}

}
