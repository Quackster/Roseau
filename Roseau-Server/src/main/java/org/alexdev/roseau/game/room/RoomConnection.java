package org.alexdev.roseau.game.room;

import org.alexdev.roseau.game.room.model.Position;

public class RoomConnection {
	private int roomId;
	private int toId;
	private Position door;

	public RoomConnection(int roomId, int toId, Position door) {
		super();
		this.roomId = roomId;
		this.toId = toId;
		this.door = door;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public int getToId() {
		return toId;
	}

	public void setToId(int toId) {
		this.toId = toId;
	}

	public Position getDoorPosition() {
		return door;
	}

}
