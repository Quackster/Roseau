package org.alexdev.roseau.game.room;

import java.util.ArrayList;
import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.IEntity;
import org.alexdev.roseau.game.room.model.RoomModel;
import org.alexdev.roseau.game.room.settings.RoomState;
import org.alexdev.roseau.game.room.settings.RoomType;

public class RoomData {

	private int id;
	private RoomType roomType;
	private int ownerId;
	private String ownerName;
	private String name;
	private RoomState state;
	private String password;
	private String thumbnail;
	private int usersNow;
	private int usersMax;
	private String description;
	private String model;
	private String wall;
	private String floor;

	private Room room;
	
	public RoomData(Room room) {
		
		this.room = room;
	}
	
	public void fill(int id, RoomType type, int ownerId, String ownerName, String name, int state, String password, int usersNow, int usersMax, String description, String model, String wall,String floor) {

		this.id = id;
		this.roomType = type;
		this.ownerId = ownerId;
		this.ownerName = ownerName;
		this.name = name;
		this.state = RoomState.getState(state);
		this.password = password;
		this.usersNow = usersNow;
		this.usersMax = usersMax;
		this.description = description;
		this.model = model;
		this.wall = wall;
		this.floor = floor;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RoomState getState() {
		return state;
	}

	public void setState(int state) {
		this.state = RoomState.getState(state);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWall() {
		return wall;
	}

	public void setWall(String wall) {
		this.wall = wall;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public Integer getWallHeight() {
		return -1;
	}

	public int getId() {
		return id;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setUsersMax(int usersMax) {
		this.usersMax = usersMax;
	}

	public int getUsersMax() {
		return this.usersMax;
	}

	public int getUsersNow() {

		if (this.room.getUsers() == null) {
			this.room.setUsers(new ArrayList<IEntity>());
		}

		this.usersNow = this.room.getUsers().size();
		return usersNow;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public RoomType getRoomType() {
		return roomType;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public RoomModel getModel() {
		return Roseau.getDataAccess().getRoom().getModel(this.model);
	}

	public List<Integer> getRights() {
		return new ArrayList<Integer>();
	}
}
