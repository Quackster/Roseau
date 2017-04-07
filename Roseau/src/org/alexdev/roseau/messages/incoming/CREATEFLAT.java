package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.FLATCREATED;
import org.alexdev.roseau.server.messages.ClientMessage;

public class CREATEFLAT implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		// Kick off public room user
		Player publicRoomPlayer = Roseau.getGame().getPlayerManager().getIdenticalPlayer(player.getDetails().getId(), player.getNetwork().getConnectionId());

		if (publicRoomPlayer != null) {
			//publicRoomPlayer.getNetwork().close();
		}

		String floor = reader.getArgument(1, "/");
		String roomName = reader.getArgument(2, "/");
		String roomModel = reader.getArgument(3, "/");
		String roomState = reader.getArgument(4, "/");
		
		if (!floor.equals("first floor")) {
			player.kickAllConnections();
			return;
		}

		int state = 0;

		if (roomState.equals("closed")) {
			state = 1;
		}

		if (roomState.equals("password")) {
			state = 2;
		}

		if (!roomModel.equals("model_a") && 
				!roomModel.equals("model_b") && 
				!roomModel.equals("model_c") && 
				!roomModel.equals("model_d") && 
				!roomModel.equals("model_e") && 
				!roomModel.equals("model_f")) {
			
			// Possibru scripter? HAX! KICK THEM!!!
			player.kickAllConnections();
			return;
		}

		Room room = Roseau.getDataAccess().getRoom().createRoom(player, roomName, "", roomModel, state);
		player.setLastCreatedRoom(room);

		player.send(new FLATCREATED(room));

	}

}
