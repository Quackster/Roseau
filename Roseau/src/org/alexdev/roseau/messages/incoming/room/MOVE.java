package org.alexdev.roseau.messages.incoming.room;

import java.util.LinkedList;

import org.alexdev.icarus.game.pathfinder.Pathfinder;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.model.Point;
import org.alexdev.roseau.game.room.player.RoomUser;
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
		
		/*if (x == 11 && y == 33) {
			
			Room room = Roseau.getGame().getRoomManager().getRoomByPort(120);
			room.loadRoom(player);
		}*/
		
		if (player.getRoomUser().getRoom().getData().getModel().isBlocked(x, y)) {
			return;
		}
		
		if (player.getRoomUser().getPosition().sameAs(new Point(x, y))) {
			return;
		}

		System.out.println("walk req (" + x + ", " + y + ")");
		
		RoomUser roomUser = player.getRoomUser();
		roomUser.getGoal().setX(x);
		roomUser.getGoal().setY(y);

		LinkedList<Point> path = Pathfinder.makePath(player);

		if (path == null) {
			System.out.println("lol112233");
			return;
		}

		if (path.size() == 0) {
			System.out.println("lol123");
			return;
		}
		
		roomUser.setPath(path);
		roomUser.setWalking(true);
	}

}
