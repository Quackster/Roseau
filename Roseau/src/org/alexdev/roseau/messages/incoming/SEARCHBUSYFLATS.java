package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.navigator.NavigatorRequest;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.BUSY_FLAT_RESULTS;
import org.alexdev.roseau.server.messages.ClientMessage;

public class SEARCHBUSYFLATS implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		player.send(new BUSY_FLAT_RESULTS(Roseau.getGame().getRoomManager().getPopularRooms(), NavigatorRequest.POPULAR_ROOMS));
	}

}
