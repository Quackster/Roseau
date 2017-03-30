package org.alexdev.roseau.game.room;

import java.util.ArrayList;
import java.util.List;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.entity.IEntity;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.player.RoomUser;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;

public class Room {

	private int privateId;
	private boolean disposed;

	private RoomData data;
	private List<IEntity> entities;

	public Room() {
		this.data = new RoomData(this);
		this.entities = new ArrayList<IEntity>();
	}

	public void loadRoom(Player player) {

		RoomUser roomUser = player.getRoomUser();

		roomUser.setRoom(this);
		roomUser.setLoadingRoom(true);
		roomUser.getStatuses().clear();

		int floorData = Integer.parseInt(this.data.getFloor());
		int wallData = Integer.parseInt(this.data.getWall());

		if (floorData > 0) {

		}

		if (wallData > 0) {
			
		}

		if (roomUser.getRoom().hasRights(player.getDetails().getId(), true)) {


		} else {

		}

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

		if (this.data.getOwnerId() == userId) {
			return true;
		} else {
			if (!ownerCheckOnly) {
				return this.data.getRights().contains(userId);
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

				if (Roseau.getGame().getPlayerManager().findById(this.data.getOwnerId()) == null 
						&& this.data.getRoomType() == RoomType.PRIVATE) { 

					this.data = null;
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
		return data;
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

}
