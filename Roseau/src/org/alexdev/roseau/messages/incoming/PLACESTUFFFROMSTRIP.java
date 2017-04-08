package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class PLACESTUFFFROMSTRIP implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		int itemId = Integer.valueOf(reader.getArgument(0));
		int x = Integer.valueOf(reader.getArgument(1));
		int y = Integer.valueOf(reader.getArgument(2));
		
		Item item = player.getInventory().getItem(itemId);
		
		if (item == null) {
			return;
		}
		
		Room room = player.getRoomUser().getRoom();
		
		if (room == null) {
			return;
		}
		
		if (!room.hasRights(player.getDetails().getId(), false)) {
			return;
		}
		
		item.setX(x);
		item.setY(y);
		room.getMapping().addItem(item);
		
		player.getInventory().removeItem(item);
		player.getInventory().refresh();
	}

}
