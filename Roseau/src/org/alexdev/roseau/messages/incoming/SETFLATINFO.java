package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class SETFLATINFO implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		/*[04/07/2017 11:47:36] [ROSEAU] >> [2119211522] Received: SETFLATINFO / /36/description=ggggggggggg
password=wef
allsuperuser=0*/
		
		Room room = Roseau.getGame().getRoomManager().getRoomByID(Integer.valueOf(reader.getArgument(1, "/")));
		
		if (!room.hasRights(player.getDetails().getID(), true)) {
			return;
		}
		
		String message = reader.getMessageBody().replace("/" + room.getData().getID() + "/", "");
		
		String description = message.split(Character.toString((char)13))[0].substring(12); // remove "description=" prefix
		String password = message.split(Character.toString((char)13))[1].substring(9); // remove "password=" prefix
		

		room.getData().setDescription(description);
		room.getData().setPassword(password);
		room.getData().save();
	}
}
