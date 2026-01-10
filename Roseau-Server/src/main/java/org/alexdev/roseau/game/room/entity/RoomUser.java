package org.alexdev.roseau.game.room.entity;

import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.RoomConnection;
import org.alexdev.roseau.game.room.RoomTile;
import org.alexdev.roseau.game.room.model.RoomModel;
import org.alexdev.roseau.game.room.model.Rotation;
import org.alexdev.roseau.messages.outgoing.CHAT;
import org.alexdev.roseau.messages.outgoing.PH_NOTICKETS;
import org.alexdev.roseau.messages.outgoing.STATUS;
import org.alexdev.roseau.messages.outgoing.USERS;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.GameVariables;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.interactors.BlankInteractor;
import org.alexdev.roseau.game.pathfinder.Pathfinder;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.game.room.model.Position;
import org.oldskooler.simplelogger4j.SimpleLog;

public class RoomUser {
    private static final SimpleLog logger = SimpleLog.of(RoomUser.class);

	private int danceId;
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
	private int lookResetTime;
	private Item currentItem;

	private int afkTimer;

	private boolean kickWhenStop;

	public RoomUser(Entity entity) {
		this.dispose();
		this.entity = entity;
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
		this.path = new java.util.LinkedList<>();

		this.position = null;
		this.goal = null;

		this.position = new Position(0, 0, 0);
		this.goal = new Position(0, 0, 0);

		this.currentItem = null;
		this.needsUpdate = false;
		this.isWalking = false;
		this.danceId = 0;
		this.timeUntilNextDrink = -1;

		this.resetAfkTimer();

	}

	public void walkItemTrigger() {

		if (!(this.entity instanceof Player)) {
			return;
		}

		Item item = this.room.getMapping().getHighestItem(this.position.getX(), this.position.getY());

		if (item == null) {
			return;
		}

		item.getInteraction().onTrigger((Player)this.entity);
	}

	public void stopWalking() {
		this.removeStatus("mv");
		this.isWalking = false;
		
		this.goal = null;
		this.next = null;

		if (!(this.entity instanceof Player)) {
			this.needsUpdate = true;
			return;
		}
		
		Player player = (Player)this.entity;

		if (this.kickWhenStop) {
			player.dispose();
			player.kick();
			return;
		}

		RoomConnection connectionRoom = this.room.getMapping().getRoomConnection(this.position.getX(), this.position.getY());

		if (connectionRoom != null) {
			Room room = Roseau.getGame().getRoomManager().getRoomByID(connectionRoom.getToId());

			if (room != null) {

				if (this.room != null) {
					this.room.leaveRoom(player, false);
				}

				player.getNetwork().setServerPort(room.getData().getServerPort());

				if (connectionRoom.getDoorPosition() != null) {
					room.loadRoom(player, connectionRoom.getDoorPosition(), connectionRoom.getDoorPosition().getRotation());
				} else {
					room.loadRoom(player);
				}

				this.setNeedUpdate(true);

				return;
			} else {
				logger.warn("Tried to connect player to room ID: " + connectionRoom.getToId() + " but no room could be found.");
			}
		}

		Item item = this.room.getMapping().getHighestItem(this.position.getX(), this.position.getY());

		boolean noCurrentItem = false;

		if (item != null) {
			if (item.canWalk(this.entity, position)) {
				this.currentItem = item;
				this.currentItemTrigger();
			} else {
				noCurrentItem = true;
			}
		} else {
			noCurrentItem = true;
		}

		if (noCurrentItem) {
			this.currentItem = null;
		}

	}

	public void currentItemTrigger() {
		Optional.ofNullable(this.currentItem)
			.map(Item::getInteraction)
			.ifPresentOrElse(
				interaction -> interaction.onStoppedWalking((Player) this.entity),
				() -> new BlankInteractor(null).onStoppedWalking((Player) this.entity)
			);

		this.needsUpdate = true;
	}

	public boolean walkTo(int x, int y) {
		if (this.room == null) {
			return false;
		}

		Optional.ofNullable(this.next)
			.ifPresent(next -> {
				this.position.setX(next.getX());
				this.position.setY(next.getY());
				this.updateNewHeight(this.position);
			});

		if (!this.canWalk) {
			return false;
		}

		if (this.kickWhenStop) {
			this.kickWhenStop = false;
		}

		if (GameVariables.DEBUG_ENABLE) {
			Optional.ofNullable(this.room.getMapping().getHighestItem(x, y))
				.ifPresent(item -> logger.debug(item.getDefinition().getSprite() + " - " + item.getDefinition().getId() + " - " + item.getCustomData()));
		}

		this.resetAfkTimer();

		if (!this.room.getMapping().isValidTile(this.entity, x, y)) {
			return false;
		}

		Optional.ofNullable(this.room.getMapping().getHighestItem(x, y))
			.filter(item -> (item.getDefinition().getSprite().equals("poolLift") || item.getDefinition().getSprite().equals("poolQueue")))
			.filter(item -> !(entity.getDetails().getTickets() > 0))
			.ifPresent(item -> {
				if (entity.getType() == EntityType.PLAYER) {
					((Player)entity).send(new PH_NOTICKETS());
				}
			});

		if (this.position.isMatch(new Position(x, y))) {
			return false;
		}

		this.goal = new Position(x, y);
		return Optional.ofNullable(Pathfinder.makePath(this.entity))
			.filter(path -> !path.isEmpty())
			.map(path -> {
				this.path = path;
				this.isWalking = true;
				return true;
			})
			.orElse(false);
	}

	public void chat(String talkMessage) {
		this.room.send(new CHAT("CHAT", this.entity.getDetails().getName(), talkMessage));
	}

	public void chat(String header, String talkMessage) {
		this.room.send(new CHAT(header, this.entity.getDetails().getName(), talkMessage));
	}


	public void chat(final String response, final int delay) {
		final Room room = this.room;
		final PlayerDetails details = this.entity.getDetails();

		Runnable task = () -> room.send(new CHAT("CHAT", details.getName(), response));

		Roseau.getGame().getScheduler().schedule(task, GameVariables.BOT_RESPONSE_DELAY, TimeUnit.MILLISECONDS);
	}

	/*
	 * Rotation calculator taken from Blunk v5
	 */
	public void lookTowards(Position look) {
		if (this.isWalking) {
			return;
		}

		int diff = this.getPosition().getRotation() - Rotation.calculateDirection(this.position.getX(), this.position.getY(), look.getX(), look.getY());


		if ((this.getPosition().getRotation() % 2) == 0) {

			if (diff > 0) {
				this.position.setHeadRotation(this.getPosition().getRotation() - 1);
			} else if (diff < 0) {
				this.position.setHeadRotation(this.getPosition().getRotation() + 1);
			} else {
				this.position.setHeadRotation(this.getPosition().getRotation());
			}
		}

		this.needsUpdate = true;
	}

	public void removeStatus(String key) {
		this.statuses.remove(key);
	}

	public void setStatus(String key, String value, boolean infinite, long duration, boolean sendUpdate) {

		if (key.equals("carryd")) {
			this.timeUntilNextDrink = GameVariables.CARRY_DRINK_INTERVAL;
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
		return this.danceId != 0;
	}

	public int getDanceId() {
		return danceId;
	}

	public void setDanceId(int danceId) {
		this.danceId = danceId;
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

	public boolean needsUpdate() {
		return needsUpdate;
	}

	public void setNeedUpdate(boolean needsWalkUpdate) {
		this.needsUpdate = needsWalkUpdate;
	}

	public Room getRoom() {
		return room;
	}

	public int getRoomID() {
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

	public void setCanWalk(boolean flag) {
		this.canWalk = flag;
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

	public int getLookResetTime() {
		return lookResetTime;
	}

	public void setLookResetTime(int lookResetTime) {
		this.lookResetTime = lookResetTime;
	}

	public Item getCurrentItem() {
		return currentItem;
	}

	public void setCurrentItem(Item currentItem) {
		this.currentItem = currentItem;
	}

	public boolean isKickWhenStop() {
		return kickWhenStop;
	}

	public void setKickWhenStop(boolean kickWhenStop) {
		this.kickWhenStop = kickWhenStop;
	}

	public int getAfkTimer() {
		return afkTimer;
	}

	public void setAfkTimer(int afkTimer) {
		this.afkTimer = afkTimer;
	}

	public void resetAfkTimer() {
		this.afkTimer = GameVariables.AFK_ROOM_KICK;
	}


	/**
	 * Update new height.
	 * 
	 * @param position the position to update height for
	 */
	public void updateNewHeight(Position position) {
		if (this.room == null) {
			return;
		}

		RoomTile tile = this.room.getMapping().getTile(position.getX(), position.getY());

		if (tile == null) {
			return;
		}

		double height = tile.getHeight();
		double oldHeight = this.position.getZ();

		if (height != oldHeight) {
			this.position.setZ(height);
			this.needsUpdate = true;
		}
	}
}
