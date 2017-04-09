package org.alexdev.roseau.messages.incoming;

import java.util.LinkedList;
import org.alexdev.roseau.game.pathfinder.Pathfinder;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MOVE implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		if (!player.getRoomUser().canWalk()) {
			Log.println("debug 1");
			return;
		}
		
		if (reader.getArgumentAmount() < 2) {
			Log.println("debug 2");
			return;
		}

		int x = Integer.valueOf(reader.getArgument(0));
		int y = Integer.valueOf(reader.getArgument(1));

		if (player.getRoomUser().getRoom() == null) {
			Log.println("debug 3");
			return;
		}
		
		/*Item item = player.getRoomUser().getRoom().getMapping().getHighestItem(x, y);

		if (item != null) {
			Log.println(item.getDefinition().getSprite() + " - " + item.getDefinitionId());
		}*/

		if (!player.getRoomUser().getRoom().getMapping().isValidTile(player, x, y)) {
			return;
		}

		if (player.getRoomUser().getPosition().sameAs(new Position(x, y))) {
			return;
		}

		RoomUser roomEntity = player.getRoomUser();
		roomEntity.getGoal().setX(x);
		roomEntity.getGoal().setY(y);

		LinkedList<Position> path = Pathfinder.makePath(player);

		if (path == null) {
			Log.println("debug 4");
			return;
		}

		if (path.size() == 0) {
			Log.println("debug 5");
			return;
		}

		roomEntity.setPath(path);
		roomEntity.setWalking(true);
		
		/*if (x == 9 && y == 8) {
			player.send(new OBJECTS_WORLD("lobby"));
		}*/
	}

}
