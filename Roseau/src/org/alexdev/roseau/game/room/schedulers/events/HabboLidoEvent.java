package org.alexdev.roseau.game.room.schedulers.events;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.schedulers.RoomEvent;
import org.alexdev.roseau.messages.outgoing.SHOWPROGRAM;

public class HabboLidoEvent extends RoomEvent {

	private int followingID = -1;
	private int cameraType = -1;

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

				if (this.cameraType != 1) {
					this.cameraType = 1;
					this.room.send(new SHOWPROGRAM(new String[] {"cam1", "setcamera", Integer.toString(this.cameraType) }));	
				}
			}

			if (cameraEffect == 2) {
				if (this.cameraType != 2) {
					this.cameraType = 2;
					this.room.send(new SHOWPROGRAM(new String[] {"cam1", "setcamera", Integer.toString(this.cameraType) }));	
				}
			}
		}

		this.increaseTicked();
	}

	private void findNewTarget() {

		List<Player> players = room.getPlayers();

		if (players.isEmpty()) {
			return;
		}

		Player player = null;

		if (players.size() == 1) {
			player = players.get(0);
		} else {
			player = players.get(Roseau.getUtilities().getRandom().nextInt(players.size()));
		}

		if (this.followingID != player.getDetails().getID()) {
			this.followingID = player.getDetails().getID();
			this.room.send(new SHOWPROGRAM(new String[] {"cam1", "targetcamera", player.getDetails().getUsername() }));
		}
	}

}
