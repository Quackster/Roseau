package org.alexdev.roseau.game.room.schedulers.events;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.schedulers.RoomEvent;
import org.alexdev.roseau.messages.outgoing.SHOWPROGRAM;

public class HabboLidoEvent extends RoomEvent {

	private int followingID = -1;

	public HabboLidoEvent(Room room) {
		super(room);
	}

	@Override
	public void tick() {

		if (this.room.getPlayerByID(this.followingID) == null) {
			this.findNewTarget();
		}

		if (this.canTick(9)) {

			int cameraEffect = Roseau.getUtilities().getRandom().nextInt(3);

			if (cameraEffect == 0) {
				this.findNewTarget();
			}

			if (cameraEffect == 1) {
				this.room.send(new SHOWPROGRAM(new String[] {"cam1", "setcamera", "1" }));	
			}

			if (cameraEffect == 2) {
				this.room.send(new SHOWPROGRAM(new String[] {"cam1", "setcamera", "2" }));	
			}
		}

		this.increaseTicked();
	}

	private void findNewTarget() {

		if (room.getPlayers().isEmpty()) {
			return;
		}

		Player player = room.getPlayers().get(Roseau.getUtilities().getRandom().nextInt(room.getPlayers().size()));

		if (this.followingID != player.getDetails().getID()) {
			this.followingID = player.getDetails().getID();
			this.room.send(new SHOWPROGRAM(new String[] {"cam1", "targetcamera", player.getDetails().getUsername() }));
		}
	}

}
