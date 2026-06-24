package org.alexdev.roseau.dao.mysql.entity;

import org.oldskooler.entity4j.annotations.Column;
import org.oldskooler.entity4j.annotations.Entity;
import org.oldskooler.entity4j.annotations.Id;

@Entity(table = "room_public_connections")
public class RoomPublicConnectionEntity {

	@Id(auto = true)
	private int id;

	@Column(name = "room_id")
	private int roomId;

	@Column(name = "to_id")
	private int toId;

	private String coordinates;

	@Column(name = "door_x")
	private int doorX;

	@Column(name = "door_y")
	private int doorY;

	@Column(name = "door_z")
	private int doorZ;

	@Column(name = "door_rotation")
	private int doorRotation;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public int getDoorX() {
		return doorX;
	}

	public void setDoorX(int doorX) {
		this.doorX = doorX;
	}

	public int getDoorY() {
		return doorY;
	}

	public void setDoorY(int doorY) {
		this.doorY = doorY;
	}

	public int getDoorZ() {
		return doorZ;
	}

	public void setDoorZ(int doorZ) {
		this.doorZ = doorZ;
	}

	public int getDoorRotation() {
		return doorRotation;
	}

	public void setDoorRotation(int doorRotation) {
		this.doorRotation = doorRotation;
	}
}
