package org.alexdev.roseau.messages.incoming.login;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.messages.outgoing.login.SYSTEMBROADCAST;
import org.alexdev.roseau.messages.outgoing.user.USEROBJECT;
import org.alexdev.roseau.server.messages.ClientMessage;

public class LOGIN implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		String username = reader.getArgument(0);
		String password = reader.getArgument(1);
		
		boolean authenticated = Roseau.getDataAccess().getPlayer().login(player, username, password);
		
		if (authenticated) {
			//player.send(new USEROBJECT(player.getDetails()));
			
		} else {
			player.send(new SYSTEMBROADCAST("Login incorrect"));
			player.getNetwork().close();
			return;
		}
	}
}
