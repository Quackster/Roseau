package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class CARRYITEM implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		String item = reader.getMessageBody();
		item = item.replace('/', '?');

		RoomUser roomUser = player.getRoomUser();
		roomUser.removeStatus("dance");
		roomUser.setStatus("carryd", " " + item, false, Roseau.getUtilities().getHabboConfig().get("Player", "carry.drink.time", Integer.class), true);
	}

}
