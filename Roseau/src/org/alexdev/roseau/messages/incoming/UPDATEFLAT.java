package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class UPDATEFLAT implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		// TODO Auto-generated method stub
		
		
		/*[04/07/2017 19:18:56] [ROSEAU] >> [-2121264494] Received: UPDATEFLAT / /42/dddddddddddd/open/0
[04/07/2017 19:18:56] [ROSEAU] >> [-2121264494] Received: SETFLATINFO / /42/description=vedww
password=123
allsuperuser=1
wordfilter_disable=*/
		
		Room room = Roseau.getGame().getRoomManager().getRoomByID(Integer.valueOf(reader.getArgument(1, "/")));
		
		if (room == null) {
			return;
		}
		
		if (!room.hasRights(player.getDetails().getID(), true)) {
			return;
		}
		
		
		String roomName = reader.getArgument(2, "/");
		String roomState = reader.getArgument(3, "/");
		
		int state = 0;

		if (roomState.equals("closed")) {
			state = 1;
		}

		if (roomState.equals("password")) {
			state = 2;
		}

		room.getData().setName(roomName);
		room.getData().setState(state);
		room.getData().save();

	}

}
