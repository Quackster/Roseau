package org.alexdev.roseau.messages.incoming.login;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.messages.outgoing.login.SYSTEMBROADCAST;
import org.alexdev.roseau.messages.outgoing.room.ACTIVE_OBJECTS;
import org.alexdev.roseau.messages.outgoing.room.HEIGHTMAP;
import org.alexdev.roseau.messages.outgoing.room.OBJECTS_WORLD;
import org.alexdev.roseau.server.messages.ClientMessage;

public class LOGIN implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		String username = reader.getArgument(0);
		String password = reader.getArgument(1);

		boolean publicRoomAccess = reader.getArgumentAmount() > 2;

		if (publicRoomAccess) {
			
			Room room = Roseau.getGame().getRoomManager().getRoomByPort(player.getNetwork().getServerPort());
			
			player.send(new ACTIVE_OBJECTS());
			player.send(new OBJECTS_WORLD(room));
			
			if (room.getData().getName().equals("Main Lobby")) {
				player.send(new HEIGHTMAP(room.getData().getModel().getHeightMap()));
			}
			
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
