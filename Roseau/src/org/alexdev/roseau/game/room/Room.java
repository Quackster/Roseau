package org.alexdev.roseau.game.room;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.game.room.entity.RoomUserStatus;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.game.room.model.Rotation;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.messages.outgoing.ACTIVE_OBJECTS;
import org.alexdev.roseau.messages.outgoing.FLATPROPERTY;
import org.alexdev.roseau.messages.outgoing.HEIGHTMAP;
import org.alexdev.roseau.messages.outgoing.ITEMS;
import org.alexdev.roseau.messages.outgoing.LOGOUT;
import org.alexdev.roseau.messages.outgoing.OBJECTS_WORLD;
import org.alexdev.roseau.messages.outgoing.ROOM_READY;
import org.alexdev.roseau.messages.outgoing.STATUS;
import org.alexdev.roseau.messages.outgoing.USERS;
import org.alexdev.roseau.messages.outgoing.YOUARECONTROLLER;
import org.alexdev.roseau.messages.outgoing.YOUAREOWNER;
import org.alexdev.roseau.server.messages.Response;
import org.alexdev.roseau.server.messages.SerializableObject;

public class Room implements Runnable, SerializableObject {

	private int orderId = -1;
	private boolean disposed;

	private RoomData roomData;
	private RoomMapping roomMapping;

	private List<Entity> entities = new ArrayList<Entity>();
	
	private ConcurrentHashMap<Integer, Item> passiveObjects;
	private ConcurrentHashMap<Integer, Item> items;
	private ScheduledFuture<?> tickTask = null;
	private List<Integer> rights;

	public Room() {
		this.roomData = new RoomData(this);
		this.roomMapping = new RoomMapping(this);
		this.entities = new ArrayList<Entity>();
	}

	@Override
	public void run() {

		try {
			if (this.disposed || this.entities.size() == 0) {
				return;
			}

			List<Entity> update_entities = new ArrayList<Entity>();
			List<Entity> entities = this.getEntities();

			for (int i = 0; i < entities.size(); i++) {

				Entity entity = entities.get(i);

				if (entity != null) {
					if (entity.getRoomUser() != null) {

						this.processEntity(entity);

						RoomUser roomEntity = entity.getRoomUser();

						if (roomEntity.playerNeedsUpdate()) {
							update_entities.add(entity);
						}
					}
				}
			}

			if (update_entities.size() > 0) {
				this.send(new STATUS(update_entities));

				for (Entity entity : update_entities) {
					
					if (entity.getRoomUser().isWalking()) {
						if (entity.getRoomUser().getNext() != null) {

							Position next = entity.getRoomUser().getNext();
							
							entity.getRoomUser().getPosition().setZ(this.roomData.getModel().getHeight(next.getX(), next.getY()));
							entity.getRoomUser().getPosition().setX(next.getX());
							entity.getRoomUser().getPosition().setY(next.getY());
						}
					}
					
					entity.getRoomUser().walkItemTrigger();
					
					if (entity.getRoomUser().playerNeedsUpdate()) {
						entity.getRoomUser().setNeedUpdate(false);
					}
				}
			}

		} catch (Exception e) {


		}
	}

	private void processEntity(Entity entity) {

		RoomUser roomEntity = entity.getRoomUser();
		
		if (roomEntity.isWalking()) {
			
			if (roomEntity.getPath().size() > 0) {

				Position next = roomEntity.getPath().pop();

				roomEntity.removeStatus("lay");
				roomEntity.removeStatus("sit");

				int rotation = Rotation.calculateHumanMoveDirection(roomEntity.getPosition().getX(), roomEntity.getPosition().getY(), next.getX(), next.getY());
				double height = this.roomData.getModel().getHeight(next.getX(), next.getY());

				roomEntity.setRotation(rotation, false);

				roomEntity.setStatus("mv", " " + next.getX() + "," + next.getY() + "," + (int)height);
				roomEntity.setNeedUpdate(true);
				roomEntity.setNext(next);

			}
			else {
				roomEntity.setNext(null);
				roomEntity.setNeedUpdate(true);
			}
		}
		
		for (Entry<String, RoomUserStatus> set : entity.getRoomUser().getStatuses().entrySet()) {

			RoomUserStatus statusEntry = set.getValue();

			if (!statusEntry.isInfinite()) {
				statusEntry.tick();

				if (statusEntry.getDuration() == 0) {
					entity.getRoomUser().removeStatus(statusEntry.getKey());
					entity.getRoomUser().setNeedUpdate(true);
				}
			}
		}
	}


	public void loadRoom(Player player) {

		RoomUser roomEntity = player.getRoomUser();

		roomEntity.setRoom(this);
		roomEntity.getStatuses().clear();

		if (this.roomData.getModel() != null) {
			roomEntity.getPosition().setX(this.roomData.getModel().getDoorX());
			roomEntity.getPosition().setY(this.roomData.getModel().getDoorY());
			roomEntity.getPosition().setZ(this.roomData.getModel().getDoorZ());
			roomEntity.setRotation(this.roomData.getModel().getDoorRot(), false);


		}

		if (this.roomData.getModel() == null) {
			Log.println("Could not load heightmap for room model '" + this.roomData.getModelName() + "'");
		}	
		
		if (this.entities.size() > 0) {
			this.send(player.getRoomUser().getUsersComposer());
			player.getRoomUser().sendStatusComposer();
		} else {
			this.init();
		}

		if (this.roomData.getRoomType() == RoomType.PRIVATE) {

			player.send(new ROOM_READY(this.roomData.getDescription()));

			int wallData = Integer.parseInt(this.roomData.getWall());
			int floorData = Integer.parseInt(this.roomData.getFloor());

			if (wallData > 0) {
				player.send(new FLATPROPERTY("wallpaper", this.roomData.getWall()));
			}	

			if (floorData > 0) {
				player.send(new FLATPROPERTY("floor", this.roomData.getFloor()));
			}

			if (this.roomData.getOwnerId() == player.getDetails().getId()) {	
				player.send(new YOUAREOWNER());
				roomEntity.setStatus("flatctrl", " useradmin");
			} else if (this.hasRights(player.getDetails().getId(), false)) {
				player.send(new YOUARECONTROLLER());
			}
		}
	    
		if (this.roomData.getModel() != null) {
			player.send(new HEIGHTMAP(this.roomData.getModel().getHeightMap()));
		}

		player.send(new OBJECTS_WORLD(this.roomData.getModelName(), this.passiveObjects));
		player.send(new ACTIVE_OBJECTS(this));
		player.send(new ITEMS(this));
		

		player.send(new USERS(this.entities));
		player.send(new STATUS(this.entities));

		player.send(player.getRoomUser().getUsersComposer());
		player.send(player.getRoomUser().getStatusComposer());

		this.entities.add(player);
	}

	public void send(OutgoingMessageComposer response, boolean checkRights) {

		if (this.disposed) {
			return;
		}

		for (Player player : this.getPlayers()) {
			player.send(response);
		}
	}


	public void leaveRoom(Player player, boolean hotelView) {

		if (hotelView) {

		}

		if (this.entities != null) {
			this.entities.remove(player);
		}

		player.getInventory().dispose();
		
		RoomUser roomUser = player.getRoomUser();
		roomUser.dispose();

		this.send(new LOGOUT(player.getDetails().getUsername()));

		this.dispose();
	}

	public boolean hasRights(int userId, boolean ownerCheckOnly) {

		if (this.roomData.getOwnerId() == userId) {
			return true;
		} else {
			if (!ownerCheckOnly) {
				return this.rights.contains(userId);
			}
		}

		return false;
	}

	public void init() {
		this.disposed = false;

		if (this.tickTask == null) {
			this.tickTask = Roseau.getGame().getScheduler().scheduleAtFixedRate(this, 0, 500, TimeUnit.MILLISECONDS);
		}
		
		this.passiveObjects = Roseau.getDataAccess().getItem().getPublicRoomItems(this.roomData.getModelName());
		
		if (this.roomData.getRoomType() == RoomType.PUBLIC) {

			for (Item item : this.passiveObjects.values()) {
				item.setRoomId(this.getData().getId());
			}
			
		} else {
			this.rights = Roseau.getDataAccess().getRoom().getRoomRights(this.roomData.getId());
			this.items = Roseau.getDataAccess().getItem().getRoomItems(this.roomData.getId());
		}
		
		this.roomMapping.regenerateCollisionMaps();
	}

	public void dispose(boolean forceDisposal) {

		try {

			if (forceDisposal) {
				this.clearData();
				this.entities = null;
				Roseau.getGame().getRoomManager().getLoadedRooms().remove(this);

			} else {

				if (this.disposed) {
					return;
				}

				if (this.getPlayers().size() > 0) {
					return;
				}

				this.clearData();

				if (Roseau.getGame().getPlayerManager().getById(this.roomData.getOwnerId()) == null 
						&& this.roomData.getRoomType() == RoomType.PRIVATE) { 

					this.roomData = null;
					this.entities = null;
					this.disposed = true;

					Roseau.getGame().getRoomManager().getLoadedRooms().remove(this);
				}
			}

		} catch (Exception e) {
			Log.exception(e);
		}

	}

	private void clearData() {

		if (this.entities != null) {
			this.entities.clear();
		}		

		if (this.tickTask != null) {
			this.tickTask.cancel(true);
			this.tickTask = null;
		}
	}



	public void send(OutgoingMessageComposer response) {

		if (this.disposed) {
			return;
		}

		for (Player player : this.getPlayers()) {
			player.send(response);
		}
	}

	public List<Player> getPlayers() {

		List<Player> sessions = new ArrayList<Player>();

		for (Entity entity : this.getEntities(EntityType.PLAYER)) {
			Player player = (Player)entity;
			sessions.add(player);
		}

		return sessions;
	}

	public List<Entity> getEntities(EntityType type) {
		List<Entity> e = new ArrayList<Entity>();

		for (Entity entity : this.entities) {
			if (entity.getType() == type) {
				e.add(entity);
			}
		}

		return e;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public RoomData getData() {
		return roomData;
	}

	public void save() {
		Roseau.getDataAccess().getRoom().updateRoom(this);
	}

	public void dispose() {
		this.dispose(false);
	}

	public boolean isValidStep(Entity player, Position current, Position neighbour, boolean isFinalMove) {

		int mapSizeX = this.roomData.getModel().getMapSizeX();
		int mapSizeY = this.roomData.getModel().getMapSizeY();

		if (neighbour.getX() >= mapSizeX || neighbour.getY() >= mapSizeY) {
			return false;
		}

		if (current.getX() >= mapSizeX || current.getY() >= mapSizeY) {
			return false;
		}

		if (neighbour.getX() < 0 || neighbour.getY() < 0) {
			return false;
		}

		if (current.getX() < 0 || current.getY() < 0) {
			return false;
		}

		if (this.roomData.getModel().isBlocked(current.getX(), current.getY())) {
			return false;
		}

		if (this.roomData.getModel().isBlocked(neighbour.getX(), neighbour.getY())) {
			return false;
		}

		if (current.getX() != this.roomData.getModel().getDoorX() && current.getY() != this.roomData.getModel().getDoorY()) {
			if (!this.roomMapping.isValidTile(player, current.getX(), current.getY())) {
				return false;
			}
		}

		double heightCurrent = this.roomData.getModel().getHeight(current);
		double heightNeighour = this.roomData.getModel().getHeight(neighbour);

		Item currentItem = this.roomMapping.getHighestItem(current.getX(), current.getY());
		Item neighbourItem = this.roomMapping.getHighestItem(neighbour.getX(), neighbour.getY());
		Item playerItem = this.roomMapping.getHighestItem(player.getRoomUser().getPosition().getX(), player.getRoomUser().getPosition().getY());

		if (currentItem != null) {
			if (currentItem.getDefinition().getSprite().equals("poolEnter") || currentItem.getDefinition().getSprite().equals("poolExit")) {
				return player.getDetails().getPoolFigure().length() > 0;
			}
		}

		if (heightCurrent > heightNeighour) {
			if ((heightCurrent - heightNeighour) >= 3.0) {
				Log.println("error difference: " + (heightCurrent - heightNeighour));
				return false;
			}
		}

		if (heightNeighour > heightCurrent) {

			if ((heightNeighour - heightCurrent) >= 1.2) {
				Log.println("error difference: " + (heightNeighour - heightCurrent));
				return false;
			}
		}

		/*if (currentItem != null && playerItem != null) {
			if (playerItem == currentItem) {
				return true;
			}
		}

		if (neighbourItem != null && playerItem != null) {
			if (playerItem ==neighbourItem) {
				return true;
			}
		}*/


		if (!current.sameAs(player.getRoomUser().getPosition())) {
			if (currentItem != null) {
				if (!isFinalMove) {

					if (currentItem != null && playerItem != null) {
						if (playerItem == currentItem) {
							return true;
						}
					}

					if (neighbourItem != null && playerItem != null) {
						if (playerItem == neighbourItem) {
							return true;
						}
					}

					return false;
				}

				if (isFinalMove) {
					return currentItem.canWalk(player);

				}
			}
		}

		return true;
	}


	public ConcurrentHashMap<Integer, Item> getItems() {
		return items;
	}

	public RoomMapping getMapping() {
		return roomMapping;
	}


	public void setRoomMapping(RoomMapping roomMapping) {
		this.roomMapping = roomMapping;
	}


	@Override
	public void serialise(Response response) {
		response.appendNewArgument(String.valueOf(this.roomData.getId()));
		response.appendPartArgument(this.roomData.getName());
		response.appendPartArgument(this.roomData.getOwnerName());
		response.appendPartArgument(this.roomData.getState().toString());
		response.appendPartArgument("");//this.roomData.getPassword()); // password...
		response.appendPartArgument("floor1");
		response.appendPartArgument(Roseau.getServerIP());
		response.appendPartArgument(Roseau.getServerIP());
		response.appendPartArgument(String.valueOf(Roseau.getServerPort() - 1));
		response.appendPartArgument(String.valueOf(this.roomData.getUsersNow()));
		response.appendPartArgument("null");
		response.appendPartArgument(this.roomData.getDescription());
	}

	public Item getItem(int id) {
		
		if (this.items.containsKey(id)) {
			return this.items.get(id);
		}
		
		return null;
	}

	public ConcurrentHashMap<Integer, Item> getPassiveObjects() {
		return passiveObjects;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}


}
