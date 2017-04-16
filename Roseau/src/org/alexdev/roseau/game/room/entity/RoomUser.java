package org.alexdev.roseau.game.room.entity;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.RoomConnection;
import org.alexdev.roseau.game.room.model.RoomModel;
import org.alexdev.roseau.game.room.model.Rotation;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.outgoing.CHAT;
import org.alexdev.roseau.messages.outgoing.OPEN_UIMAKOPPI;
import org.alexdev.roseau.messages.outgoing.STATUS;
import org.alexdev.roseau.messages.outgoing.USERS;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.ItemDefinition;
import org.alexdev.roseau.game.pathfinder.Pathfinder;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.game.room.model.Position;
import com.google.common.collect.Lists;

public class RoomUser {

	private int virtualID;
	private int lastChatID;
	private int danceID;
	private int timeUntilNextDrink;

	private Position position;
	private Position goal;
	private Position next = null;
	
	private ConcurrentHashMap<String, RoomUserStatus> statuses;
	private LinkedList<Position> path;

	private Room room;

	private boolean isWalking = false;
	private boolean needsUpdate = false;
	private boolean canWalk = true;

	private Entity entity;

	public RoomUser(Entity entity) {
		this.dispose();
		this.entity = entity;
	}

	public void walkItemTrigger() {
		if (this.entity instanceof Player) {

			Item item = this.room.getMapping().getHighestItem(this.position.getX(), this.position.getY());

			if (item != null) {
				ItemDefinition definition = item.getDefinition();

				if (definition.getSprite().equals("poolEnter")) {
					this.setStatus("swim", "", true, -1);
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

	public void stopWalking() {

		this.removeStatus("mv");

		this.isWalking = false;
		this.needsUpdate = true;

		if (this.entity instanceof Player) {

			Player player = (Player)this.entity;
			
			if (this.room.getData().getRoomType() == RoomType.PUBLIC) {

				RoomConnection connectionRoom = this.room.getMapping().getRoomConnection(this.position.getX(), this.position.getY());
				
				if (connectionRoom != null) {
					Room room = Roseau.getGame().getRoomManager().getRoomByID(connectionRoom.getToID());
					
					if (room != null) {
						player.getNetwork().setServerPort(room.getData().getServerPort());
						
						if (connectionRoom.getDoorPosition() != null) {
							room.loadRoom(player, connectionRoom.getDoorPosition(), connectionRoom.getDoorPosition().getBodyRotation());
						} else {
							room.loadRoom(player);
						}
						
						return;
					} else {
						Log.println("Tried to connect player to room ID: " + connectionRoom.getToID() + " but no room could be found.");
					}
				}
			}
		}

		Item item = this.room.getMapping().getHighestItem(this.position.getX(), this.position.getY());

		if (item == null) {
			return;	
		}

		ItemDefinition definition = item.getDefinition();

		if (definition == null) {
			return;
		}

		if (definition.getSprite().equals("poolBooth")) {

			item.showProgram("close");
			item.lockTiles(); // users cant walk on this tile

			((Player) this.entity).send(new OPEN_UIMAKOPPI());
			((Player) this.entity).getRoomUser().toggleWalkAbility();
		}

		if (definition.getBehaviour().isCanSitOnTop()) {
			this.getPosition().setRotation(item.getRotation(), false);
			this.removeStatus("dance");
			this.setStatus("sit", " " + String.valueOf(this.position.getZ() + definition.getHeight()), true, -1);
		}

	}

	public void walkTo(Position position) {
		this.walkTo(position.getX(), position.getY());
	}
	
	public void walkTo(int x, int y) {
		
		/*double height = player.getRoomUser().getRoom().getData().getModel().getHeight(x, y);
		
		Log.println("height: " + height);*/

		if (this.room == null) {
			return;
		}
		
		if (!this.canWalk) {
			return;
		}
		
		Item item = this.room.getMapping().getHighestItem(x, y);

		if (item != null) {
			Log.println(item.getDefinition().getSprite() + " - " + item.getDefinition().getID());
		}

		if (!this.room.getMapping().isValidTile(this.entity, x, y)) {
			return;
		}

		if (this.position.isMatch(new Position(x, y))) {
			return;
		}

		this.goal.setX(x);
		this.goal.setY(y);

		LinkedList<Position> path = Pathfinder.makePath(this.entity);

		if (path == null) {
			return;
		}

		if (path.size() == 0) {
			return;
		}

		this.path = path;
		this.isWalking = true;
	}

	public void chat(String talkMessage) {
		this.room.send(new CHAT("CHAT", this.entity.getDetails().getUsername(), talkMessage));
	}
	
	public void chat(String header, String talkMessage) {
		this.room.send(new CHAT(header, this.entity.getDetails().getUsername(), talkMessage));
	}


	public void chat(final String response, final int delay) {
		
		final Room room = this.room;
		final PlayerDetails details = this.entity.getDetails();
		
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	room.send(new CHAT("CHAT", details.getUsername(), response));
		            }
		        }, 
		        delay * 1000
		);
	}
	
	public void lookTowards(Position look) {

		if (this.isWalking) {
			return;
		}

		int diff = this.getPosition().getBodyRotation() - Rotation.calculateHumanDirection(this.position.getX(), this.position.getY(), look.getX(), look.getY());


		if ((this.getPosition().getBodyRotation() % 2) == 0)
		{
			if (diff > 0)
			{
				this.position.setHeadRotation(this.getPosition().getBodyRotation() - 1);
			}
			else if (diff < 0)
			{
				this.position.setHeadRotation(this.getPosition().getBodyRotation() + 1);
			}
			else
			{
				this.position.setHeadRotation(this.getPosition().getBodyRotation());
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

		this.lastChatID = 0;
		this.virtualID = -1;
		this.danceID = 0;
		this.timeUntilNextDrink = -1;

	}

	public void removeStatus(String key) {
		this.statuses.remove(key);
	}

	public void setStatus(String key, String value, boolean infinite, long duration, boolean sendUpdate) {
		
		if (key.equals("carryd")) {
			this.timeUntilNextDrink = 12;
		}
		
		this.setStatus(key, value, infinite, duration);
		
		if (sendUpdate) {
			this.needsUpdate = true;
		}
	}
	
	public void setStatus(String key, String value, boolean infinite, long duration) {

		if (this.containsStatus(key)) {
			this.removeStatus(key);
		}

		this.statuses.put(key, new RoomUserStatus(key, value, infinite, duration));
	}

	public void forceStopWalking() {

		this.removeStatus("mv");
		this.path.clear();
	}

	public boolean containsStatus(String string) {
		return this.statuses.containsKey(string);
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
		return this.danceID != 0;
	}
	public int getVirtualID() {
		return virtualID;
	}

	public void setVirtualID(int virtualID) {
		this.virtualID = virtualID;
	}

	public int getLastChatID() {
		return lastChatID;
	}

	public void setLastChatID(int lastChatID) {
		this.lastChatID = lastChatID;
	}

	public int getDanceID() {
		return danceID;
	}

	public void setDanceID(int danceID) {
		this.danceID = danceID;
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

	public int getRoomID() {
		return (room == null ? 0 : room.getData().getID());
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

	public int getTimeUntilNextDrink() {
		return timeUntilNextDrink;
	}

	public void setTimeUntilNextDrink(int timeUntilNextDrink) {
		this.timeUntilNextDrink = timeUntilNextDrink;
	}



}
