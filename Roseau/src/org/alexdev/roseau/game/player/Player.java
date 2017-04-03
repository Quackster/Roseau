package org.alexdev.roseau.game.player;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.entity.IEntity;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.entity.RoomEntity;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.IPlayerNetwork;

public class Player implements IEntity {

	private String machineId;
	private PlayerDetails details;
	private IPlayerNetwork network;
	private RoomEntity roomEntity;
	
	private Player createdFlat = null;
	private List<Room> rooms;

	public Player(IPlayerNetwork network) {
		this.network = network;
		this.details = new PlayerDetails(this);
		this.roomEntity = new RoomEntity(this);
	}

	public void cacheUserData() {

		// Load player rooms 
		this.rooms = Roseau.getDataAccess().getRoom().getPlayerRooms(this.details, true);
	}
	
	public void dispose() {

		if (this.roomEntity != null) {
			if (this.roomEntity.getRoom() != null) {
				this.roomEntity.getRoom().leaveRoom(this, false);
			}
		}
	}
		
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

	public String getMachineId() {
		return machineId;
	}

	public PlayerDetails getDetails() {
		return details;
	}

	public IPlayerNetwork getNetwork() {
		return network;
	}

	public void send(OutgoingMessageComposer response) {
		this.network.send(response);
	}
	
	public Player getCreatedFlat() {
		return createdFlat;
	}

	public void setCreatedFlat(Player player) {
		this.createdFlat = player;
	}

	public List<Room> getRooms() {
		return rooms;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}
	
	@Override
	public RoomEntity getRoomUser() {
		return this.roomEntity;
	}
}
