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
import org.alexdev.roseau.messages.outgoing.SHOWPROGRAM;
import org.alexdev.roseau.messages.outgoing.STATUS;
import org.alexdev.roseau.messages.outgoing.USERS;
import org.alexdev.roseau.messages.outgoing.YOUARECONTROLLER;
import org.alexdev.roseau.messages.outgoing.YOUAREOWNER;
import org.alexdev.roseau.server.messages.Response;
import org.alexdev.roseau.server.messages.SerializableObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Room implements Runnable, SerializableObject {

	private int orderID = -1;
	private boolean disposed;

	private RoomData roomData;
	private RoomMapping roomMapping;

	private List<Entity> entities;

	private ConcurrentHashMap<Integer, Item> passiveObjects;
	private ConcurrentHashMap<Integer, Item> items;
	
	private ScheduledFuture<?> tickTask = null;
	private List<Integer> rights;

	public Room() {
		this.roomData = new RoomData(this);
		this.roomMapping = new RoomMapping(this);
		
		this.entities = Lists.newArrayList();
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
		this.loadRoom(player, this.roomData.getModel().getDoorPosition(), this.roomData.getModel().getDoorRot());
	}
	
	public void loadRoom(Player player, Position door, int rotation) {

		RoomUser roomEntity = player.getRoomUser();
		
		if (player.getRoomUser().getRoom() != null) {
			player.getRoomUser().getRoom().leaveRoom(player, false);
		}

		roomEntity.setRoom(this);
		roomEntity.getStatuses().clear();

		if (this.roomData.getModel() != null) {
			roomEntity.getPosition().setX(door.getX());
			roomEntity.getPosition().setY(door.getY());
			roomEntity.getPosition().setZ(door.getZ());
			roomEntity.setRotation(rotation, false);
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

			if (this.roomData.getOwnerID() == player.getDetails().getID()) {	
				player.send(new YOUAREOWNER());
				roomEntity.setStatus("flatctrl", " useradmin");
			} else if (this.hasRights(player.getDetails().getID(), false)) {
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
		
		if (this.roomData.getModelName().equals("bar_b")) {
			
			//http://i.imgur.com/XQZ3b1y.png
			
			// 1-6
			//this.send(new SHOWPROGRAM(new String[] {"lamp", "setlamp", "2"}));
			
			
			this.send(new SHOWPROGRAM(new String[] {"df1", "setfloora", String.valueOf(Roseau.getUtilities().getRandom().nextInt(14) + 1)}));
			this.send(new SHOWPROGRAM(new String[] {"df1", "setfloorb", String.valueOf(Roseau.getUtilities().getRandom().nextInt(14) + 1)}));
			
			this.send(new SHOWPROGRAM(new String[] {"df2", "setfloora", String.valueOf(Roseau.getUtilities().getRandom().nextInt(14) + 1)}));
			this.send(new SHOWPROGRAM(new String[] {"df2", "setfloorb", String.valueOf(Roseau.getUtilities().getRandom().nextInt(14) + 1)}));
			
			this.send(new SHOWPROGRAM(new String[] {"df3", "setfloora", String.valueOf(Roseau.getUtilities().getRandom().nextInt(14) + 1)}));
			this.send(new SHOWPROGRAM(new String[] {"df3", "setfloorb", String.valueOf(Roseau.getUtilities().getRandom().nextInt(14) + 1)}));
		}
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

	public boolean hasRights(int userID, boolean ownerCheckOnly) {

		if (this.roomData.getOwnerID() == userID) {
			return true;
		} else {
			if (!ownerCheckOnly) {
				return this.rights.contains(userID);
			}
		}

		return false;
	}

	public void init() {
		this.disposed = false;

		if (this.tickTask == null) {
			this.tickTask = Roseau.getGame().getScheduler().scheduleAtFixedRate(this, 0, 500, TimeUnit.MILLISECONDS);
		}

		this.passiveObjects = Roseau.getDataAccess().getItem().getPublicRoomItems(this.roomData.getModelName(), this.roomData.getID());

		if (this.roomData.getRoomType() == RoomType.PRIVATE) {
			this.rights = Roseau.getDataAccess().getRoom().getRoomRights(this.roomData.getID());
			this.items = Roseau.getDataAccess().getItem().getRoomItems(this.roomData.getID());
		}
		
		this.roomMapping.regenerateCollisionMaps();
	}

	public void dispose(boolean forceDisposal) {

		try {

			if (forceDisposal) {
				this.clearData();
				this.entities = null;
				Roseau.getGame().getRoomManager().getLoadedRooms().remove(this.getData().getID());

			} else {

				if (this.disposed) {
					return;
				}

				if (this.getPlayers().size() > 0) {
					return;
				}

				this.clearData();

				if (Roseau.getGame().getPlayerManager().getByID(this.roomData.getOwnerID()) == null 
						&& this.roomData.getRoomType() == RoomType.PRIVATE) { 


					this.entities = null;
					this.disposed = true;

					Roseau.getGame().getRoomManager().getLoadedRooms().remove(this.getData().getID());
					this.roomData = null;
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

		if (this.roomData.getModel().invalidXYCoords(current.getX(), current.getY())) {
			return false;
		}

		if (this.roomData.getModel().invalidXYCoords(neighbour.getX(), neighbour.getY())) {
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

		Item currentItem = this.roomMapping.getHighestItem(current.getX(), current.getY());

		if (currentItem != null) {
			if (currentItem.getDefinition().getSprite().equals("poolEnter") || currentItem.getDefinition().getSprite().equals("poolExit")) {
				return player.getDetails().getPoolFigure().length() > 0;
			}
		}

		if (!this.roomData.getModel().hasDisabledHeightCheck()) {
		
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
		
		}

		if (!current.sameAs(this.roomData.getModel().getDoorPosition())) {

			if (!this.roomMapping.isValidTile(player, current.getX(), current.getY())) {
				return false;
			}

			if (!current.sameAs(player.getRoomUser().getPosition())) {
				if (currentItem != null) {
					if (!isFinalMove) {
						return currentItem.getDefinition().getBehaviour().isCanStandOnTop();
					}

					if (isFinalMove) {
						return currentItem.canWalk(player);

					}
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
		response.appendNewArgument(String.valueOf(this.roomData.getID()));
		response.appendPartArgument(this.roomData.getName());
		response.appendPartArgument(this.roomData.getOwnerName());
		response.appendPartArgument(this.roomData.getState().toString());
		response.appendPartArgument("");//this.roomData.getPassword()); // password...
		response.appendPartArgument("floor1");
		response.appendPartArgument(Roseau.getServerIP());
		response.appendPartArgument(Roseau.getServerIP());
		response.appendPartArgument(String.valueOf(Roseau.getPrivateServerPort()));
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

	public int getOrderID() {
		return orderID;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}
}
