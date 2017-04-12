package org.alexdev.roseau.game.room;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.room.model.RoomModel;
import org.alexdev.roseau.game.room.settings.RoomState;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.server.IServerHandler;

public class RoomData {

	private int id;
	private RoomType roomType;
	private int ownerID;
	private String ownerName;
	private String name;
	private RoomState state;
	private String password;
	private int usersNow;
	private int usersMax;
	private String description;
	private String model;
	private String clazz;
	private String wall;
	private String floor;

	private Room room;
	private IServerHandler serverHandler = null;
	private boolean hidden;
	
	public RoomData(Room room) {
		this.room = room;
	}
	
	public void fill(int id, boolean hidden, RoomType type, int ownerID, String ownerName, String name, int state, String password, int usersNow, int usersMax, String description, String model, String clazz, String wall,String floor) {

		
		this.id = id;
		this.hidden = hidden;
		this.roomType = type;
		this.ownerID = ownerID;
		this.ownerName = ownerName;
		this.name = name;
		this.state = RoomState.getState(state);
		this.password = password;
		this.usersNow = usersNow;
		this.usersMax = usersMax;
		this.description = description;
		this.model = model;
		this.clazz = clazz;
		this.wall = wall;
		this.floor = floor;
		
		
		try {
			if (type == RoomType.PUBLIC) {
				
				this.serverHandler = Class.forName(Roseau.getSocketConfiguration().get("extension.socket.entry"))
						.asSubclass(IServerHandler.class)
						.getDeclaredConstructor(String.class)
						.newInstance(String.valueOf(id));
			
				Log.println("[ROOM] [" + this.name + "] Starting public room server on port: " + this.getServerPort());
				
		
				this.serverHandler.setIp(Roseau.getServerIP());
				this.serverHandler.setPort(this.getServerPort());
				this.serverHandler.listenSocket();
			}
		} catch (Exception e) {
			Log.exception(e);
		}
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

	public int getID() {
		return id;
	}

	public int getOwnerID() {
		return ownerID;
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
		this.usersNow = this.room.getPlayers().size();
		return usersNow;
	}

	public RoomType getRoomType() {
		return roomType;
	}

	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
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

	public String getCCT() {
		return clazz;
	}

	public String getModelName() {
		return this.model;
	}

	public int getServerPort() {
		return (this.id + Roseau.getServerPort());
	}

	public IServerHandler getServerHandler() {
		return serverHandler;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void save() {
		Roseau.getDataAccess().getRoom().updateRoom(this.room);
	}

}
