package org.alexdev.roseau.game.player;

import java.util.List;
import java.util.stream.Collectors;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.inventory.Inventory;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.IPlayerNetwork;

public class Player implements Entity {

	private String machineID;
	private PlayerDetails details;
	private IPlayerNetwork network;
	private RoomUser roomEntity;
	private Inventory inventory;
	private Room lastCreatedRoom;

	public Player(IPlayerNetwork network) {
		this.network = network;
		this.details = new PlayerDetails(this);
		this.roomEntity = new RoomUser(this);
		this.inventory = new Inventory(this);
		this.lastCreatedRoom = null;
	}
	
	public void login() {
		
	}
	
	public Player getPrivateRoomPlayer() {
		
		try {
			return Roseau.getGame()
					.getPlayerManager()
					.getPlayers()
					.values().stream()
					.filter(s -> s.getDetails().getID() == this.details.getID() && 
					s.getNetwork().getServerPort() == (Roseau.getPrivateServerPort())).findFirst().get();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void dispose() {

		if (this.roomEntity != null) {
			if (this.roomEntity.getRoom() != null) {
				this.roomEntity.getRoom().leaveRoom(this, false);
			}
		}
		
		this.inventory.dispose();
	}
	
	public void send(OutgoingMessageComposer response) {
		this.network.send(response);
	}
	
	public void kick() {
		this.network.close();
	}
	
	public void kickAllConnections() {
		
		try {
			
			List<Player> players = Roseau.getGame().getPlayerManager().getPlayers().values().stream().filter(s -> s.getDetails().getID() == this.details.getID()).collect(Collectors.toList());
			
			for (Player player : players) {
				player.kick();
			}
			
		} catch (Exception e) {
			return;
		}
	}
		
	public void setMachineID(String machineID) {
		this.machineID = machineID;
	}

	public String getMachineID() {
		return machineID;
	}

	public PlayerDetails getDetails() {
		return details;
	}

	public IPlayerNetwork getNetwork() {
		return network;
	}

	public List<Room> getRooms() {
		return Roseau.getDataAccess().getRoom().getPlayerRooms(this.details, true);
	}
	
	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}
	
	@Override
	public RoomUser getRoomUser() {
		return this.roomEntity;
	}

	public Room getLastCreatedRoom() {
		return lastCreatedRoom;
	}

	public void setLastCreatedRoom(Room lastCreatedRoom) {
		this.lastCreatedRoom = lastCreatedRoom;
	}

	public Inventory getInventory() {
		return inventory;
	}


}
