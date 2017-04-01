package org.alexdev.roseau.messages.incoming.room.user;

import java.util.LinkedList;

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
		
		if (player.getRoomEntity().getRoom() == null) {
			return;
		}
		
		if (!player.getRoomEntity().getRoom().getRoomMapping().isValidTile(x, y)) {
			return;
		}
		
		if (player.getRoomEntity().getPosition().sameAs(new Point(x, y))) {
			return;
		}

		RoomEntity roomEntity = player.getRoomEntity();
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
