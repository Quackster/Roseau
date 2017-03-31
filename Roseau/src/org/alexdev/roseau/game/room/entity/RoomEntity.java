package org.alexdev.roseau.game.room.entity;

import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.text.Position;

import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.model.RoomModel;
import org.alexdev.roseau.messages.outgoing.room.STATUS;
import org.alexdev.roseau.messages.outgoing.room.USERS;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.IEntity;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.model.Point;
import org.alexdev.roseau.util.GameSettings;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class RoomEntity {

	private int virtualId;
	private int lastChatId;
	private int danceId;

	private Point position;
	private Point goal;
	private Point next = null;

	private int rotation;
	private int headRotation;

	private HashMap<String, String> statuses;
	private LinkedList<Point> path;

	private Room room;

	private boolean isWalking = false;
	private boolean needsUpdate = false;

	private IEntity entity;

	private long chatFloodTimer;
	private int chatCount;

	public RoomEntity(IEntity entity) {
		this.dispose();
		this.entity = entity;
	}

	public void setStatus(String key, String value) {

		if (value.length() > 0) {
			this.statuses.put(key, value);
		} else {
			this.statuses.remove(key);
		}
		
		this.needsUpdate = true;
	}

	public void walk() {

		if (this.isWalking) {
			if (this.next != null) {

				Point next = this.next;
	            this.position.setZ(this.getRoom().getData().getModel().getHeight(next.getX(), next.getY()));
	            this.position = next;
				
			}
		}

	}

	public void stopWalking() {
		// TODO Auto-generated method stub
		
	}

	public void chat(String message, int bubble, int count, boolean shout, boolean spamCheck) {

		boolean isStaff = false;
		Player player = null;

		if (this.entity instanceof Player) {

			player = (Player)this.entity;
			isStaff = player.getDetails().hasFuse("moderator");
		}

		if (spamCheck) {
			if (Roseau.getUtilities().getTimestamp() < this.chatFloodTimer && this.chatCount >= GameSettings.MAX_CHAT_BEFORE_FLOOD) {

				if (!isStaff) {
					if (player != null) {

					}
					return;
				}
			}
		}


		//this.room.send(new TalkMessageComposer(this, shout, message, count, bubble));

		if (spamCheck) {
			if (!player.getDetails().hasFuse("moderator")) {

				if (Roseau.getUtilities().getTimestamp() > this.chatFloodTimer && this.chatCount >= GameSettings.MAX_CHAT_BEFORE_FLOOD) {
					this.chatCount = 0;
				} else {
					this.chatCount = this.chatCount + 1;
				}

				this.chatFloodTimer = (Roseau.getUtilities().getTimestamp() + GameSettings.CHAT_FLOOD_SECONDS);

			}
		}
	}

	public void dispose() {

		if (this.statuses != null) {
			this.statuses.clear();
		}

		if (this.path != null) {
			this.path.clear();
		}

		this.statuses = null;
		this.path = null;

		this.statuses = Maps.newHashMap();
		this.path = Lists.newLinkedList();

		this.position = null;
		this.goal = null;

		this.position = new Point(0, 0, 0);
		this.goal = new Point(0, 0, 0);

		this.lastChatId = 0;
		this.virtualId = -1;
		this.danceId = 0;

	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public Point getGoal() {
		return goal;
	}

	public void setGoal(Point goal) {
		this.goal = goal;
	}

	public STATUS getStatusComposer() {
		return new STATUS(this.entity);
	}

	public USERS getUsersComposer() {
		return new USERS(this.entity);
	}

	public void updateStatus() {
		this.room.send(new STATUS(this.entity));
	}

	public boolean isDancing() {
		return this.danceId != 0;
	}
	public int getVirtualId() {
		return virtualId;
	}

	public void setVirtualId(int virtualId) {
		this.virtualId = virtualId;
	}

	public int getLastChatId() {
		return lastChatId;
	}

	public void setLastChatId(int lastChatId) {
		this.lastChatId = lastChatId;
	}

	public int getDanceId() {
		return danceId;
	}

	public void setDanceId(int danceId) {
		this.danceId = danceId;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public int getHeadRotation() {
		return headRotation;
	}

	public void setRotation(int rotation, boolean headOnly) {

		this.headRotation = rotation;

		if (!headOnly) {
			this.rotation = rotation;
		}
	}

	public HashMap<String, String> getStatuses() {
		return statuses;
	}

	public LinkedList<Point> getPath() {
		return path;
	}


	public void setPath(LinkedList<Point> path) {

		if (this.path != null) {
			this.path.clear();
		}

		this.path = path;
	}

	public boolean needsUpdate() {
		return needsUpdate;
	}

	public void setNeedUpdate(boolean needsWalkUpdate) {
		this.needsUpdate = needsWalkUpdate;
	}

	public Room getRoom() {
		return room;
	}

	public int getRoomId() {
		return (room == null ? 0 : room.getData().getId());
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public RoomModel getModel() {
		return room.getData().getModel();
	}
	public boolean isWalking() {
		return isWalking;
	}

	public void setWalking(boolean isWalking) {
		this.isWalking = isWalking;
	}

	public IEntity getEntity() {
		return entity;
	}

	public Point getNext() {
		return next;
	}

	public void setNext(Point next) {
		this.next = next;
	}
}
