package org.alexdev.roseau.game.room.entity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.model.RoomModel;
import org.alexdev.roseau.game.room.model.Rotation;
import org.alexdev.roseau.messages.outgoing.OPEN_UIMAKOPPI;
import org.alexdev.roseau.messages.outgoing.STATUS;
import org.alexdev.roseau.messages.outgoing.USERS;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.ItemDefinition;
import org.alexdev.roseau.game.pathfinder.Pathfinder;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.model.Position;
import com.google.common.collect.Lists;

public class RoomUser {

	private int virtualId;
	private int lastChatId;
	private int danceId;

	private Position position;
	private Position goal;
	private Position next = null;

	private int bodyRotation;
	private int headRotation;

	private ConcurrentHashMap<String, RoomUserStatus> statuses;
	private LinkedList<Position> path;

	private Room room;

	private boolean isWalking = false;
	private boolean needsUpdate = false;
	private boolean canWalk = true;

	private Entity entity;

	//private long chatFloodTimer;
	//private int chatCount;

	public RoomUser(Entity entity) {
		this.dispose();
		this.entity = entity;
	}

	public void removeStatus(String key) {
		this.statuses.remove(key);
	}

	public void setStatus(String key, String value) {
		this.setStatus(key, value, false);
	}
	
	public void setStatus(String key, String value, boolean needs_update) {
		this.setStatus(key, value, true, -1, needs_update);
	}
	
	public void setStatus(String key, String value, boolean infinite, int duration) {
		this.setStatus(key, value, infinite, duration, false);
	}

	public void setStatus(String key, String value, boolean infinite, int duration, boolean needs_update) {
		
		if (this.containsStatus(key)) {
			this.removeStatus(key);
		}
		
		this.statuses.put(key, new RoomUserStatus(key, value, infinite, duration));
		
		if (needs_update) {
			this.needsUpdate = true;
		}
	}

	
	public boolean containsStatus(String string) {
		return this.statuses.containsKey(string);
	}

	public void walkedPositionUpdate() {
		if (this.entity instanceof Player) {
			
			Item item = this.room.getMapping().getHighestItem(this.position.getX(), this.position.getY());

			if (item != null) {
				ItemDefinition definition = item.getDefinition();

				if (definition.getSprite().equals("poolEnter")) {
					this.setStatus("swim", "");
					this.poolInteractor(item, "enter");
					return;

				} else if (definition.getSprite().equals("poolExit")) {
					this.removeStatus("swim");
					this.poolInteractor(item, "exit");
					return;
				}
			}
		}
	}

	public void poolInteractor(Item item, String program) {
		this.isWalking = false;
		this.next = null;
		
		this.forceStopWalking();
		
		String[] positions = item.getCustomData().split(" ", 2);
		
		Position warp = new Position(positions[0]);
		Position goal = new Position(positions[1]);
		
		this.position.setX(warp.getX());
		this.position.setY(warp.getY());
		this.position.setZ(this.room.getMapping().getTile(warp.getX(), warp.getY()).getHeight());
		this.needsUpdate = true;
		
		//this.sendStatusComposer();
		item.showProgram(program);
		
		this.goal.setX(goal.getX());
		this.goal.setY(goal.getY());
		this.goal.setZ(this.room.getMapping().getTile(goal.getX(), goal.getY()).getHeight());
		this.path.addAll(Pathfinder.makePath(this.entity));
		

		this.isWalking = true;
	}

	public void forceStopWalking() {

		this.removeStatus("mv");
		this.path.clear();
	}

	public void stopWalking() {

		this.removeStatus("mv");

		Item item = this.room.getMapping().getHighestItem(this.position.getX(), this.position.getY());

		if (item != null) {
			ItemDefinition definition = item.getDefinition();

			if (definition != null) {

				if (definition.getBehaviour().isCanSitOnTop()) {
					this.setRotation(item.getRotation(), false);

					this.removeStatus("dance");
					this.setStatus("sit", " " + String.valueOf(this.position.getZ() + definition.getHeight()));
				}

				if (this.entity instanceof Player) {
					if (definition.getSprite().equals("poolBooth")) {

						item.showProgram("close");
						item.lockTiles(); // users cant walk on this tile

						((Player) this.entity).send(new OPEN_UIMAKOPPI());
						((Player) this.entity).getRoomUser().toggleWalkAbility();
					}
				}
			}
		}

		this.isWalking = false;
		this.needsUpdate = true;
	}



	/*public void chat(String talkMessage, String header, boolean spamCheck) {

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

		this.room.send(new CHAT_MESSAGE(this, talkMessage, header));

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
	}*/

	public void lookTowards(Position look) {

		if (this.isWalking) {
			return;
		}

		int diff = this.bodyRotation - Rotation.calculateHumanDirection(this.position.getX(), this.position.getY(), look.getX(), look.getY());


		if ((this.bodyRotation % 2) == 0)
		{
			if (diff > 0)
			{
				this.headRotation = (this.bodyRotation - 1);
			}
			else if (diff < 0)
			{
				this.headRotation = (this.bodyRotation + 1);
			}
			else
			{
				this.headRotation = this.bodyRotation;
			}
		}


		this.needsUpdate = true;
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

		this.statuses = new ConcurrentHashMap<String, RoomUserStatus>();
		this.path = Lists.newLinkedList();

		this.position = null;
		this.goal = null;

		this.position = new Position(0, 0, 0);
		this.goal = new Position(0, 0, 0);

		this.needsUpdate = false;
		this.isWalking = false;

		this.lastChatId = 0;
		this.virtualId = -1;
		this.danceId = 0;

	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Position getGoal() {
		return goal;
	}

	public void setGoal(Position goal) {
		this.goal = goal;
	}
	
	public void sendStatusComposer() {
		this.room.send(new STATUS(this.entity));
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
		return bodyRotation;
	}

	public void setRotation(int rotation) {
		this.bodyRotation = rotation;
	}

	public int getHeadRotation() {
		return headRotation;
	}

	public void setRotation(int rotation, boolean headOnly) {

		this.headRotation = rotation;

		if (!headOnly) {
			this.bodyRotation = rotation;
		}
	}

	public ConcurrentHashMap<String, RoomUserStatus> getStatuses() {
		return statuses;
	}

	public LinkedList<Position> getPath() {
		return path;
	}


	public void setPath(LinkedList<Position> path) {

		if (this.path != null) {
			this.path.clear();
		}

		this.path = path;
	}

	public boolean playerNeedsUpdate() {
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

	public Entity getEntity() {
		return entity;
	}

	public Position getNext() {
		return next;
	}

	public void setNext(Position next) {
		this.next = next;
	}

	public void toggleWalkAbility() {
		this.canWalk = !this.canWalk;
	}

	public boolean canWalk() {
		return this.canWalk;
	}

}
