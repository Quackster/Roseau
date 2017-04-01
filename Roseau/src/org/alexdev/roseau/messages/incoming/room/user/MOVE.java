package org.alexdev.roseau.messages.incoming.room.user;

import java.util.LinkedList;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.pathfinder.Pathfinder;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
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
		
		if (player.getRoomEntity().getRoom().getData().getModel().isBlocked(x, y)) {
			return;
		}
		
		if (player.getRoomEntity().getPosition().sameAs(new Point(x, y))) {
			return;
		}

		System.out.println("walk req (" + x + ", " + y + ")");
		
		RoomEntity roomEntity = player.getRoomEntity();
		roomEntity.getGoal().setX(x);
		roomEntity.getGoal().setY(y);

		LinkedList<Point> path = Pathfinder.makePath(player);

		if (path == null) {
			System.out.println("lol112233");
			return;
		}

		if (path.size() == 0) {
			System.out.println("lol123");
			return;
		}
		
		roomEntity.setPath(path);
		roomEntity.setWalking(true);
	}

}
