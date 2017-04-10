package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class PLACEITEMFROMSTRIP implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		int itemId = Integer.valueOf(reader.getArgument(0));
		
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

		if (!item.getDefinition().getBehaviour().isOnWall()) {
			return;
		}
		
		String wallPosition = reader.getMessageBody().replace(itemId + " ", "");
		
		item.setWallPosition(wallPosition);
		item.save();
		
		room.getMapping().addItem(item, true);
		
		player.getInventory().removeItem(item);
	}

}
