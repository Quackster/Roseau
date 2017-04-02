package org.alexdev.roseau.messages.incoming.login;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.messages.outgoing.user.SYSTEMBROADCAST;
import org.alexdev.roseau.server.messages.ClientMessage;

public class LOGIN implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		String username = reader.getArgument(0);
		String password = reader.getArgument(1);
		
		boolean authenticated = Roseau.getDataAccess().getPlayer().login(player, username, password);

		if (authenticated) {
			
			player.getDetails().setAuthenticated(true);
			player.getDetails().setPassword(password);
			
			if (reader.getArgumentAmount() > 2) {
				Room room = Roseau.getGame().getRoomManager().getRoomByPort(player.getNetwork().getServerPort());
				room.loadRoom(player);
			} else {
				player.login();
			}
		} else {
			player.send(new SYSTEMBROADCAST("Your username or password was incorrect."));
			player.getNetwork().close();
			return;
		}
	}
}
