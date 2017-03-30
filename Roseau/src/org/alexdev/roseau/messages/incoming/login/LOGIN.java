package org.alexdev.roseau.messages.incoming.login;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.messages.outgoing.login.SYSTEMBROADCAST;
import org.alexdev.roseau.server.messages.ClientMessage;

public class LOGIN implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		String username = reader.getArgument(0);
		String password = reader.getArgument(1);

		boolean publicRoomAccess = reader.getArgumentAmount() > 2;

		if (publicRoomAccess) {

			Log.println("Public room access");

			
		} else {

			boolean authenticated = Roseau.getDataAccess().getPlayer().login(player, username, password);

			if (authenticated) {
				player.login();
			} else {
				player.send(new SYSTEMBROADCAST("Your username or password was incorrect."));
				player.getNetwork().close();
				return;
			}
		}
	}
}
