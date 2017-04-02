package org.alexdev.roseau.messages.incoming.login;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.messages.outgoing.messenger.MESSENGERSMSACCOUNT;
import org.alexdev.roseau.messages.outgoing.messenger.MESSENGERSREADY;
import org.alexdev.roseau.messages.outgoing.user.WALLETBALANCE;
import org.alexdev.roseau.server.messages.ClientMessage;

public class GETCREDITS implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {	
		player.send(new WALLETBALANCE(player.getDetails().getCredits()));
		player.send(new MESSENGERSMSACCOUNT());
		player.send(new MESSENGERSREADY());
	}

}
