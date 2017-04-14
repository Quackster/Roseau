package org.alexdev.roseau.game.room.events;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Bot;
import org.alexdev.roseau.game.room.Room;

import com.google.common.collect.Lists;

public class BotMoveRoomEvent extends RoomEvent {

	public BotMoveRoomEvent(Room room) {
		super(room);
	}

	@Override
	public void tick() {
		
		if (this.room.getBots().size() < 1) {
			return;
		}
		
		if (this.canTick(10)) { // 5 seconds 
		
			List<int[]> positions = Lists.newArrayList();
			positions.add(new int[] { 0, 12} );
			positions.add(new int[] { 0, 7} );
			positions.add(new int[] { 1, 11} );
			positions.add(new int[] { 1, 7} );
			positions.add(new int[] { 0, 10} );
			
			for (Bot bot : this.room.getBots()) {
				
				int[] position = positions.get(Roseau.getUtilities().getRandom().nextInt(positions.size() - 1));
				bot.getRoomUser().walkTo(position[0], position[1]);
			}
		}
		
		this.increaseTicked();
	}

}
