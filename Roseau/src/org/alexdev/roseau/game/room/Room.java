package org.alexdev.roseau.game.room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.entity.IEntity;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.entity.RoomEntity;
import org.alexdev.roseau.game.room.model.Point;
import org.alexdev.roseau.game.room.player.RoomUser;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.messages.outgoing.room.ACTIVE_OBJECTS;
import org.alexdev.roseau.messages.outgoing.room.HEIGHTMAP;
import org.alexdev.roseau.messages.outgoing.room.OBJECTS_WORLD;
import org.alexdev.roseau.messages.outgoing.room.STATUS;
import org.alexdev.roseau.messages.outgoing.room.USERS;

public class Room implements Runnable {

	private int privateId;
	private boolean disposed;

	private RoomData roomData;
	private List<IEntity> entities;
	private ScheduledFuture<?> tickTask = null;

	public Room() {
		this.roomData = new RoomData(this);
		this.entities = new ArrayList<IEntity>();
	}


	@Override
	public void run() {

		Log.println("loop started");

		while (true) {

			try {
				if (this.disposed ||
						this.entities.size() == 0) {
					break;
				}

				List<IEntity> update_entities = new ArrayList<IEntity>();
				List<IEntity> entities = this.getEntities();

				for (int i = 0; i < entities.size(); i++) {

					IEntity entity = entities.get(i);

					if (entity != null) {
						if (entity.getRoomUser() != null) {

							this.processEntity(entity);

							RoomEntity room_user = entity.getRoomUser();

							if (room_user.needsUpdate()) {
								update_entities.add(entity);
							}
						}
					}
				}

				if (update_entities.size() > 0) {
					this.send(new STATUS(update_entities));
				}

			} catch (Exception e) {


			}
		}

		Log.println("loop ended");
	}

	private void processEntity(IEntity entity) {
		// TODO Auto-generated method stub

	}


	public void loadRoom(Player player) {

		RoomUser roomUser = player.getRoomUser();

		roomUser.setRoom(this);
		roomUser.setLoadingRoom(true);
		roomUser.getStatuses().clear();

		roomUser.getPosition().setX(this.roomData.getModel().getDoorX());
		roomUser.getPosition().setY(this.roomData.getModel().getDoorY());
		roomUser.getPosition().setZ(this.roomData.getModel().getDoorZ());
		roomUser.setRotation(this.roomData.getModel().getDoorRot(), false);	

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

			if (roomUser.getRoom().hasRights(player.getDetails().getId(), true)) {


			} else {

			}
		}

		if (this.roomData.getModel() == null) {
			Log.println("Could not load heightmap for room model '" + this.roomData.getModelName() + "'");
			return;
		}

		if (this.entities.size() > 0) {
			this.send(player.getRoomUser().getUsersComposer());
			this.send(player.getRoomUser().getStatusComposer());
		} else {
			this.tickTask = Roseau.getGame().getScheduler().scheduleAtFixedRate(this, 0, 500, TimeUnit.MILLISECONDS);
		}

		this.entities.add(player);

		player.send(new USERS(this.entities));
		player.send(new STATUS(this.entities));

	}

	public void leaveRoom(Player player, boolean hotelView) {

		if (hotelView) {;

		}

		//this.send(new RemoveUserMessageComposer(player.getRoomUser().getVirtualId()));

		RoomUser roomUser = player.getRoomUser();

		roomUser.stopWalking(false);
		roomUser.reset();

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

	public void send(OutgoingMessageComposer response, boolean checkRights) {

		if (this.disposed) {
			return;
		}

		for (Player player : this.getUsers()) {

			if (checkRights && this.hasRights(player.getDetails().getId(), false)) {
				player.send(response);
			}
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

	public boolean isValidStep(Point point, Point tmp, boolean isFinalMove) {
		// TODO Auto-generated method stub
		return false;
	}

}
