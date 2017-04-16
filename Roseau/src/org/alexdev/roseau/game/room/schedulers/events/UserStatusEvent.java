package org.alexdev.roseau.game.room.schedulers.events;

import java.util.Map.Entry;

import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.game.room.entity.RoomUserStatus;
import org.alexdev.roseau.game.room.schedulers.RoomEvent;
import org.alexdev.roseau.log.Log;

public class UserStatusEvent extends RoomEvent {

	public UserStatusEvent(Room room) {
		super(room);
	}

	@Override
	public void tick() {

		if (this.canTick(2)) { // 1 second

			for (Entity entity : this.room.getEntities()) {

				RoomUser roomUser = entity.getRoomUser();

				for (Entry<String, RoomUserStatus> set : entity.getRoomUser().getStatuses().entrySet()) {

					RoomUserStatus statusEntry = set.getValue();

					if (!statusEntry.isInfinite()) {
						statusEntry.tick();

						if (statusEntry.getDuration() == 0) {
							entity.getRoomUser().removeStatus(statusEntry.getKey());
							
							if (statusEntry.getKey().equals("carryd")) {
								roomUser.setTimeUntilNextDrink(-1);
							}
							
							entity.getRoomUser().setNeedUpdate(true);
							continue;
						}
					}

					if (statusEntry.getKey().equals("carryd")) {

						if (roomUser.isWalking()) {
							return;
						}
						
						if (roomUser.containsStatus("dance")) {
							return;
						}
						
						if (roomUser.containsStatus("lay")) {
							return;
						}
						
						if (roomUser.getTimeUntilNextDrink() > 0) {
							roomUser.setTimeUntilNextDrink(roomUser.getTimeUntilNextDrink() - 1);
						} else {

							long remainingDuration = statusEntry.getDuration();
							String value = statusEntry.getValue();

							entity.getRoomUser().removeStatus("carryd");
							entity.getRoomUser().setStatus("drink", "", false, -1);
							entity.getRoomUser().sendStatusComposer();
							entity.getRoomUser().removeStatus("drink");
							entity.getRoomUser().setStatus("carryd", value, true, remainingDuration);
							
							roomUser.setTimeUntilNextDrink(12);
							entity.getRoomUser().setNeedUpdate(true);
						}
					}
				}
			}
		}

		this.increaseTicked();
	}
}
