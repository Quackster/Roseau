package org.alexdev.roseau.messages.incoming;

import org.oldskooler.simplelogger4j.SimpleLog;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.settings.RoomState;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.ERROR;
import org.alexdev.roseau.messages.outgoing.FLAT_LETIN;
import org.alexdev.roseau.server.messages.ClientMessage;

import java.util.Optional;

public class TRYFLAT implements MessageEvent {

    private static final SimpleLog logger = SimpleLog.of(TRYFLAT.class);

	@Override
	public void handle(Player player, ClientMessage reader) {
		int id = Integer.parseInt(reader.getArgument(1, "/"));
		String password = reader.getArgumentAmount("/") > 2 
			? reader.getArgument(2, "/") 
			: "";
		
		Optional.ofNullable(player.getRoomUser().getRoom())
			.ifPresent(room -> room.leaveRoom(player, false));

		Room room = Optional.ofNullable(Roseau.getGame().getRoomManager().getRoomByID(id))
			.orElseGet(() -> {
				Room loadedRoom = Roseau.getDao().getRoom().getRoom(id, true);
				if (loadedRoom == null) {
					logger.info("Grabbed new room from database: " + id);
				}
				return loadedRoom;
			});

		if (room == null) {
			return;
		}
		
		if (!room.hasRights(player, false)) {
			RoomState state = room.getData().getState();
			
			if (state == RoomState.PASSWORD) {
				if (!password.equals(room.getData().getPassword())) {
					player.send(new ERROR("Incorrect flat password"));
					return;
				}
			}
			
			if (state == RoomState.DOORBELL) {
				boolean received = room.ringDoorbell(player);
				if (!received) {
					player.send(new ERROR("Incorrect flat password"));
				}
				return;
			}
		}
		
		player.getRoomUser().setRoom(room);
		player.send(new FLAT_LETIN());
	}
}
