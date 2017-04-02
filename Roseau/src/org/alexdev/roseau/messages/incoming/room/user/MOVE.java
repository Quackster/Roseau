package org.alexdev.roseau.messages.incoming.room.user;

import java.util.LinkedList;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.pathfinder.Pathfinder;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.entity.RoomEntity;
import org.alexdev.roseau.game.room.model.Point;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MOVE implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		if (reader.getArgumentAmount() < 2) {
			return;
		}
		
		int x = Integer.valueOf(reader.getArgument(0));
		int y = Integer.valueOf(reader.getArgument(1));
		
		if (player.getRoomUser().getRoom() == null) {
			return;
		}
		
		if (!player.getRoomUser().getRoom().getMapping().isValidTile(x, y)) {
			
			/*Item item = player.getRoomUser().getRoom().getMapping().getHighestItem(x, y);
			
			if (item != null) {
				Log.println(item.getDefinition().getSprite() + " - " + item.getDefinitionId());
			}
			*/
			return;
		}
		
		if (player.getRoomUser().getPosition().sameAs(new Point(x, y))) {
			return;
		}

		RoomEntity roomEntity = player.getRoomUser();
		roomEntity.getGoal().setX(x);
		roomEntity.getGoal().setY(y);

		LinkedList<Point> path = Pathfinder.makePath(player);

		if (path == null) {
			return;
		}

		if (path.size() == 0) {
			return;
		}
		
		roomEntity.setPath(path);
		roomEntity.setWalking(true);
	}

}
