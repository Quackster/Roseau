package org.alexdev.roseau.game.room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.entity.IEntity;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.entity.RoomEntity;
import org.alexdev.roseau.game.room.model.Point;
import org.alexdev.roseau.game.room.model.Rotation;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.messages.outgoing.room.ACTIVE_OBJECTS;
import org.alexdev.roseau.messages.outgoing.room.HEIGHTMAP;
import org.alexdev.roseau.messages.outgoing.room.OBJECTS_WORLD;
import org.alexdev.roseau.messages.outgoing.room.STATUS;
import org.alexdev.roseau.messages.outgoing.room.USERS;
import org.alexdev.roseau.server.IServerHandler;

public class Room implements Runnable {

	private int privateId;
	private boolean disposed;

	private RoomData roomData;
	private RoomMapping roomMapping;
	
	private List<IEntity> entities;
	private List<Item> items;
	private ScheduledFuture<?> tickTask = null;

	public Room() {
		this.roomData = new RoomData(this);
		this.roomMapping = new RoomMapping(this);
		this.entities = new ArrayList<IEntity>();
	}
	

	public void loadData() {
		this.items = Roseau.getDataAccess().getItem().getPublicRoomItems(this.roomData.getModelName());
	}


	@Override
	public void run() {

		try {
			if (this.disposed || this.entities.size() == 0) {
				return;
			}

			List<IEntity> update_entities = new ArrayList<IEntity>();
			List<IEntity> entities = this.getEntities();

			for (int i = 0; i < entities.size(); i++) {

				IEntity entity = entities.get(i);

				if (entity != null) {
					if (entity.getRoomEntity() != null) {

						this.processEntity(entity);

						RoomEntity roomEntity = entity.getRoomEntity();

						if (roomEntity.needsUpdate()) {
							update_entities.add(entity);
						}
					}
				}
			}

			if (update_entities.size() > 0) {
				this.send(new STATUS(update_entities));

				for (IEntity entity : update_entities) {
					entity.getRoomEntity().walk();
					if (entity.getRoomEntity().needsUpdate()) {
						entity.getRoomEntity().setNeedUpdate(false);
					}
				}
			}

		} catch (Exception e) {


		}
	}

	private void processEntity(IEntity entity) {

		RoomEntity roomEntity = entity.getRoomEntity();

		if (roomEntity.isWalking()) {
			if (roomEntity.getPath().size() > 0) {

				Point next = roomEntity.getPath().pop();

				roomEntity.removeStatus("lay");
				roomEntity.removeStatus("");

				int rotation = Rotation.calculate(roomEntity.getPosition().getX(), roomEntity.getPosition().getY(), next.getX(), next.getY());
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
	}


	public void loadRoom(Player player) {

		RoomEntity roomEntity = player.getRoomEntity();

		roomEntity.setRoom(this);
		roomEntity.getStatuses().clear();

		roomEntity.getPosition().setX(this.roomData.getModel().getDoorX());
		roomEntity.getPosition().setY(this.roomData.getModel().getDoorY());
		roomEntity.getPosition().setZ(this.roomData.getModel().getDoorZ());
		roomEntity.setRotation(this.roomData.getModel().getDoorRot(), false);	

		if (this.roomData.getRoomType() == RoomType.PUBLIC) {
			player.send(new ACTIVE_OBJECTS());
			player.send(new OBJECTS_WORLD(this));
		}

		player.send(new HEIGHTMAP(this.roomData.getModel().getHeightMap()));

		if (this.roomData.getRoomType() == RoomType.PRIVATE) {

			int floorData = Integer.parseInt(this.roomData.getFloor());
			int wallData = Integer.parseInt(this.roomData.getWall());

			if (floorData > 0) {

			}

			if (wallData > 0) {

			}

			if (roomEntity.getRoom().hasRights(player.getDetails().getId(), true)) {


			} else {

			}
		}

		if (this.roomData.getModel() == null) {
			Log.println("Could not load heightmap for room model '" + this.roomData.getModelName() + "'");
			return;
		}

		if (this.entities.size() > 0) {
			this.send(player.getRoomEntity().getUsersComposer());
			this.send(player.getRoomEntity().getStatusComposer());
		} else {
			if (this.tickTask == null) {
				this.tickTask = Roseau.getGame().getScheduler().scheduleAtFixedRate(this, 0, 500, TimeUnit.MILLISECONDS);
				Log.println("started...");
			}
		}

		this.entities.add(player);

		player.send(new USERS(this.entities));
		player.send(new STATUS(this.entities));

	}

	public void send(OutgoingMessageComposer response, boolean checkRights) {

		if (this.disposed) {
			return;
		}

		for (Player player : this.getUsers()) {
			player.send(response);
		}
	}


	public void leaveRoom(Player player, boolean hotelView) {

		if (hotelView) {

		}

		//this.send(new RemoveUserMessageComposer(player.getRoomUser().getVirtualId()));

		RoomEntity roomUser = player.getRoomEntity();

		roomUser.setWalking(false);
		roomUser.dispose();

		if (this.entities != null) {
			this.entities.remove(player);
		}

		this.dispose();
	}

	public boolean hasRights(Player player, boolean ownerCheckOnly) {
		return this.hasRights(player.getDetails().getId(), ownerCheckOnly);
	}

	public boolean hasRights(int userId, boolean ownerCheckOnly) {

		if (this.roomData.getOwnerId() == userId) {
			return true;
		} else {
			if (!ownerCheckOnly) {
				return this.roomData.getRights().contains(userId);
			}
		}

		return false;
	}

	public void init() {

		if (this.getUsers().size() != 0) {
			return;
		}

		this.disposed = false;

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

				if (this.getUsers().size() > 0) {
					return;
				}

				this.clearData();

				if (Roseau.getGame().getPlayerManager().findById(this.roomData.getOwnerId()) == null 
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

		for (Player player : this.getUsers()) {
			player.send(response);
		}
	}

	public List<Player> getUsers() {

		List<Player> sessions = new ArrayList<Player>();

		for (IEntity entity : this.getEntities(EntityType.PLAYER)) {
			Player player = (Player)entity;
			sessions.add(player);
		}

		return sessions;
	}

	public List<IEntity> getEntities(EntityType type) {
		List<IEntity> e = new ArrayList<IEntity>();

		for (IEntity entity : this.entities) {
			if (entity.getType() == type) {
				e.add(entity);
			}
		}

		return e;
	}

	public List<IEntity> getEntities() {
		return entities;
	}

	public RoomData getData() {
		return roomData;
	}

	public void save() {
		Roseau.getDataAccess().getRoom().updateRoom(this);
	}

	public int getVirtualId() {
		this.privateId = this.privateId + 1;
		return this.privateId;
	}

	public void dispose() {
		this.dispose(false);
	}

	public void setUsers(ArrayList<IEntity> entities) {
		this.entities = entities;
	}

	public boolean isValidStep(Point current, Point neighbour, boolean isFinalMove) {

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
		
		double heightCurrent = this.roomData.getModel().getHeight(current);
		double heightNeighour = this.roomData.getModel().getHeight(neighbour);
		
		if (heightCurrent > heightNeighour) {
			
			if ((heightCurrent - heightNeighour) >= 3.0) {
				return false;
			}
		}
		
		if (heightNeighour > heightCurrent) {
			
			if ((heightNeighour - heightCurrent) >= 1.2) {
				return false;
			}
		}

		return true;
	}


	public List<Item> getItems() {
		return items;
	}


	public void setItems(List<Item> items) {
		this.items = items;
	}


	public RoomMapping getRoomMapping() {
		return roomMapping;
	}


	public void setRoomMapping(RoomMapping roomMapping) {
		this.roomMapping = roomMapping;
	}


}
