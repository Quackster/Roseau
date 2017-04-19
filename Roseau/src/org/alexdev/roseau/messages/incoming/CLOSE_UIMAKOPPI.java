package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.ItemDefinition;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class CLOSE_UIMAKOPPI implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		if (player.getRoomUser().getRoom() == null) {
			return;
		}
		
		Room room = player.getRoomUser().getRoom();
		Position position = player.getRoomUser().getPosition();
		
		Item item = room.getMapping().getHighestItem(position.getX(), position.getY());

		if (item != null) {
			ItemDefinition definition = item.getDefinition();

			if (definition == null) {
				return;
			}
			
			if (definition.getSprite().equals("poolBooth")) {
				
				item.showProgram("open");
				item.unlockTiles(); // users can now walk on this tile
				
				player.getRoomUser().setCanWalk(true);
				
			}
		}
	}
}
