package org.alexdev.roseau.messages.incoming.room;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.messages.outgoing.room.FLATCREATED;
import org.alexdev.roseau.server.messages.ClientMessage;

public class CREATEFLAT implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		player.send(new FLATCREATED());
		
		// Kick off public room user
		Player publicRoomPlayer = Roseau.getGame().getPlayerManager().getIdenticalPlayer(player.getDetails().getId(), player.getNetwork().getConnectionId());
		
		if (publicRoomPlayer != null) {
			publicRoomPlayer.getNetwork().close();
		}

	}

}
