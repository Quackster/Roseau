package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.SYSTEMBROADCAST;
import org.alexdev.roseau.server.messages.ClientMessage;

public class LOGIN implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		String username = reader.getArgument(0);
		String password = reader.getArgument(1);
		
		boolean authenticated = Roseau.getDataAccess().getPlayer().login(player, username, password);

		if (authenticated) {
			
			Player otherPlayer = Roseau.getGame().getPlayerManager().getPlayerByIdPort(player.getDetails().getId(), player.getNetwork().getServerPort(), player.getNetwork().getConnectionId());
			
			if (otherPlayer != null) {
				player.send(new SYSTEMBROADCAST("This user is already logged into the server."));
				player.getNetwork().close();
				return;
			}
			
			player.getDetails().setAuthenticated(true);
			player.getDetails().setPassword(password);
			
			if (reader.getArgumentAmount() > 2) {
				Room room = Roseau.getGame().getRoomManager().getRoomByPort(player.getNetwork().getServerPort());
				room.loadRoom(player);
			}
				
			player.cacheUserData();
			
		} else {
			player.send(new SYSTEMBROADCAST("Your username or password was incorrect."));
			player.getNetwork().close();
			return;
		}
	}
}
