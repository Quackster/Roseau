package org.alexdev.roseau.game.room;

import org.alexdev.roseau.game.room.model.Position;

public class RoomConnection 
{
	private int roomID;
	private int toID;
	
	private Position door;
	private int doorRotation;
	
	public RoomConnection(int roomID, int toID, Position door, int doorRotation) {
		super();
		this.roomID = roomID;
		this.toID = toID;
		this.door = door;
		this.doorRotation = doorRotation;
	}

	public int getRoomID() {
		return roomID;
	}

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}

	public int getToID() {
		return toID;
	}

	public void setToID(int toID) {
		this.toID = toID;
	}

	public Position getDoorPosition() {
		return door;
	}

	public int getDoorRotation() {
		return doorRotation;
	}

}
