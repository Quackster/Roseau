package org.alexdev.roseau.game.room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Bot;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.game.room.schedulers.RoomEvent;
import org.alexdev.roseau.game.room.schedulers.RoomEventScheduler;
import org.alexdev.roseau.game.room.schedulers.RoomWalkScheduler;
import org.alexdev.roseau.game.room.schedulers.events.BotMoveRoomEvent;
import org.alexdev.roseau.game.room.schedulers.events.ClubMassivaDiscoEvent;
import org.alexdev.roseau.game.room.schedulers.events.UserStatusEvent;
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
import org.alexdev.roseau.server.IServerHandler;
import org.alexdev.roseau.server.messages.Response;
import org.alexdev.roseau.server.messages.SerializableObject;

import com.google.common.collect.Lists;

public class Room implements SerializableObject {

	private int orderID = -1;
	private boolean disposed;

	private RoomData roomData;
	private RoomMapping roomMapping;

	private RoomEventScheduler roomEventScheduler;
	private RoomWalkScheduler roomWalkScheduler;

	private List<Entity> entities;

	private ConcurrentHashMap<Integer, Item> passiveObjects;
	private ConcurrentHashMap<Integer, Item> items;

	private List<Bot> bots;
	private ArrayList<RoomEvent> events;

	private ScheduledFuture<?> tickTask = null;
	private ScheduledFuture<?> eventTask = null;

	private List<Integer> rights;

	private IServerHandler serverHandler = null;


	public Room() {
		this.roomData = new RoomData(this);
		this.roomMapping = new RoomMapping(this);

		this.roomEventScheduler = new RoomEventScheduler(this);
		this.roomWalkScheduler = new RoomWalkScheduler(this);

		this.entities = Lists.newArrayList();
		this.events = Lists.newArrayList();
	}

	public void load() throws Exception {

		if (this.roomData.getRoomType() == RoomType.PUBLIC && !this.roomData.isHidden()) {

			this.serverHandler = Class.forName(Roseau.getSocketConfiguration().get("extension.socket.entry"))
					.asSubclass(IServerHandler.class)
					.getDeclaredConstructor(String.class)
					.newInstance(String.valueOf(this.roomData.getID()));

			Log.println("[ROOM] [" + this.roomData.getName() + "] Starting public room server on port: " + this.roomData.getServerPort());


			this.serverHandler.setIp(Roseau.getServerIP());
			this.serverHandler.setPort(this.roomData.getServerPort());
			this.serverHandler.listenSocket();
		}


		if (this.roomData.getRoomType() == RoomType.PRIVATE) {
			this.rights = Roseau.getDao().getRoom().getRoomRights(this.roomData.getID());
			this.items = Roseau.getDao().getItem().getRoomItems(this.roomData.getID());
		}
	}

	public void firstPlayerEntry() {
		this.disposed = false;


		if (this.tickTask == null) {
			this.tickTask = Roseau.getGame().getScheduler().scheduleAtFixedRate(this.roomWalkScheduler, 0, 500, TimeUnit.MILLISECONDS);
		}

		this.passiveObjects = Roseau.getDao().getItem().getPublicRoomItems(this.roomData.getModelName(), this.roomData.getID());
		this.bots = Roseau.getDao().getRoom().getBots(this, this.roomData.getID());

		this.roomMapping.regenerateCollisionMaps();

		if (this.roomData.getModelName().equals("bar_b")) {
			this.registerNewEvent(new ClubMassivaDiscoEvent(this));
		}

		if (this.bots.size() > 0) {
			this.entities.addAll(this.bots);
			this.registerNewEvent(new BotMoveRoomEvent(this));
		}

		this.registerNewEvent(new UserStatusEvent(this));
	}

	private void registerNewEvent(RoomEvent event) {

		if (this.eventTask == null) {
			this.eventTask = Roseau.getGame().getScheduler().scheduleAtFixedRate(this.roomEventScheduler, 0, 500, TimeUnit.MILLISECONDS);
		}

		this.events.add(event);

	}

	public void loadRoom(Player player) {

		if (this.roomData.getModel() != null) {
			this.loadRoom(player, this.roomData.getModel().getDoorPosition(), this.roomData.getModel().getDoorRot());
		} else {
			Log.println("Could not load door data for room model '" + this.roomData.getModelName() + "'");
		}
	}

	public void loadRoom(final Player player, Position door, int rotation) {

		RoomUser roomEntity = player.getRoomUser();

		roomEntity.setRoom(this);
		roomEntity.getStatuses().clear();

		if (this.roomData == null) {
			Log.println("null wot");

		}

		if (this.roomData.getModel() != null) {
			roomEntity.getPosition().setX(door.getX());
			roomEntity.getPosition().setY(door.getY());
			roomEntity.getPosition().setZ(door.getZ());
			roomEntity.getPosition().setHeadRotation(rotation);
			roomEntity.getPosition().setBodyRotation(rotation);
		}

		if (this.roomData.getModel() == null) {
			Log.println("Could not load heightmap for room model '" + this.roomData.getModelName() + "'");
		}	

		if (this.entities.size() > 0) {
			this.send(player.getRoomUser().getUsersComposer());
			player.getRoomUser().sendStatusComposer();
		} else {
			this.firstPlayerEntry();
		}

		if (this.roomData.getRoomType() == RoomType.PRIVATE) {

			player.getInventory().load();
			player.send(new ROOM_READY(this.roomData.getDescription()));

			int wallData = Integer.parseInt(this.roomData.getWall());
			int floorData = Integer.parseInt(this.roomData.getFloor());

			if (wallData > 0) {
				player.send(new FLATPROPERTY("wallpaper", this.roomData.getWall()));
			} else {
				player.send(new FLATPROPERTY("wallpaper", "201"));
			}

			if (floorData > 0) {
				player.send(new FLATPROPERTY("floor", this.roomData.getFloor()));
			} else {
				player.send(new FLATPROPERTY("floor", "0"));
			}

			if (this.roomData.getOwnerID() == player.getDetails().getID()) {	
				player.send(new YOUAREOWNER());
				roomEntity.setStatus("flatctrl", " useradmin", true, -1);
			} else if (this.hasRights(player.getDetails().getID(), false)) {
				player.send(new YOUARECONTROLLER());
			}
		}

		if (this.roomData.getModel() != null) {
			player.send(new HEIGHTMAP(this.roomData.getModel().getHeightMap()));
		}

		player.send(new OBJECTS_WORLD(this.roomData.getModelName(), this.passiveObjects));
		player.send(new ACTIVE_OBJECTS(this));

		for (Item item : this.items.values()) {
			if (item.getDefinition().getBehaviour().isOnWall()) {
				player.send(new ITEMS(item));
			}
		}


		player.send(new USERS(this.entities));
		player.send(new STATUS(this.entities));

		player.send(player.getRoomUser().getUsersComposer());
		player.send(player.getRoomUser().getStatusComposer());

		this.entities.add(player);

		if (this.roomData.getRoomType() == RoomType.PRIVATE) {
			final Item item = this.roomMapping.getHighestItem(door.getX(), door.getY());

			if (item != null) {
				if (item.getDefinition().getBehaviour().isTeleporter()) {
					roomEntity.setCanWalk(false);
					item.leaveTeleporter(player);
					return;
				}
			}
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
			if (player.getPrivateRoomPlayer() != null) { 
				player.getPrivateRoomPlayer().getNetwork().close();
			}
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

	public void dispose(boolean forceDisposal) {

		try {

			if (forceDisposal) {

				for (Player player : this.getPlayers()) {
					this.leaveRoom(player, true);
				}

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

		if (this.bots != null) {
			this.bots.clear();
		}

		if (this.events != null) {
			this.events.clear();
		}

		if (this.tickTask != null) {
			this.tickTask.cancel(true);
			this.tickTask = null;
		}

		if (this.eventTask != null) {
			this.eventTask.cancel(true);
			this.eventTask = null;
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

	public ArrayList<RoomEvent> getEvents() {
		return events;
	}

	public RoomData getData() {
		return roomData;
	}

	public boolean isDisposed() {
		return disposed;
	}

	public void setDisposed(boolean disposed) {
		this.disposed = disposed;
	}

	public void save() {
		Roseau.getDao().getRoom().updateRoom(this);
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

		if (!current.isMatch(this.roomData.getModel().getDoorPosition())) {

			if (!this.roomMapping.isValidTile(player, current.getX(), current.getY())) {
				return false;
			}

			if (!current.isMatch(player.getRoomUser().getPosition())) {
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

	public List<Bot> getBots() {
		return bots;
	}
}
