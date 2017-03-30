package org.alexdev.roseau.game.room.player;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.entity.RoomEntity;

public class RoomUser extends RoomEntity {

	private boolean inRoom;
	private boolean isLoadingRoom;

	public RoomUser(Player player) {
		super(player);
		this.isLoadingRoom = false;
		this.inRoom = false;
	}

	public void reset() {
		this.dispose();
		this.isLoadingRoom = false;
		this.inRoom = false;
	}

	public boolean inRoom() {
		return inRoom && !this.isLoadingRoom; // player is actually inside the room and not busy loading it
	}

	public void setInRoom(boolean inRoom) {
		this.inRoom = inRoom;
	}

	public boolean isLoadingRoom() {
		return isLoadingRoom;
	}

	public void setLoadingRoom(boolean isLoadingRoom) {
		this.isLoadingRoom = isLoadingRoom;
	}
}
