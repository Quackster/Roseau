package org.alexdev.roseau.game.room.entity;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.RoomConnection;
import org.alexdev.roseau.game.room.model.RoomModel;
import org.alexdev.roseau.game.room.model.Rotation;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.outgoing.CHAT;
import org.alexdev.roseau.messages.outgoing.DOOR_OUT;
import org.alexdev.roseau.messages.outgoing.JUMPINGPLACE_OK;
import org.alexdev.roseau.messages.outgoing.OPEN_GAMEBOARD;
import org.alexdev.roseau.messages.outgoing.OPEN_UIMAKOPPI;
import org.alexdev.roseau.messages.outgoing.STATUS;
import org.alexdev.roseau.messages.outgoing.USERS;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.GameVariables;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.ItemDefinition;
import org.alexdev.roseau.game.pathfinder.Pathfinder;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.game.room.model.Position;
import com.google.common.collect.Lists;

public class RoomUser {

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
	private int lookResetTime;
	private Item current_item;

	private int afkTimer;

	private boolean kickWhenStop;

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

		if (this.entity instanceof Player) {

			Player player = (Player)this.entity;

			if (this.kickWhenStop) {
				player.dispose();
				player.kick();
				return;
			}

			if (this.room.getData().getRoomType() == RoomType.PUBLIC) {

				RoomConnection connectionRoom = this.room.getMapping().getRoomConnection(this.position.getX(), this.position.getY());

				if (connectionRoom != null) {
					Room room = Roseau.getGame().getRoomManager().getRoomByID(connectionRoom.getToID());

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

						return;
					} else {
						Log.println("Tried to connect player to room ID: " + connectionRoom.getToID() + " but no room could be found.");
					}
				}
			}
		}

		Item item = this.room.getMapping().getHighestItem(this.position.getX(), this.position.getY());

		boolean no_current_item = false;

		if (item != null) {
			if (item.getDefinition().getBehaviour().isCanSitOnTop() || item.getDefinition().getBehaviour().isCanLayOnTop() || item.getDefinition().getBehaviour().isTeleporter() || item.getDefinition().getSprite().equals("poolBooth") || item.getDefinition().getSprite().equals("poolLift")) {
				this.current_item = item;
				this.currentItemTrigger();

			}
			else {
				no_current_item = true;
			}
		}
		else {
			no_current_item = true;
		}

		if (no_current_item) {
			this.current_item = null;
		}

	}

	public void currentItemTrigger() {

		Item item = this.room.getMapping().getHighestItem(this.position.getX(), this.position.getY());

		if (item == null) {
			this.current_item = null;
		}

		if (this.current_item == null) {
			this.removeStatus("sit");
			this.removeStatus("lay");
		} else {

			item = this.current_item;
			ItemDefinition definition = this.current_item.getDefinition();

			if (definition == null) {
				return;
			}

			if (definition.getSprite().equals("poolBooth")) {

				item.showProgram("close");
				item.lockTiles(); // users cant walk on this tile

				((Player) this.entity).send(new OPEN_UIMAKOPPI());
				((Player) this.entity).getRoomUser().setCanWalk(false);
			}

			if (definition.getSprite().equals("poolLift")) {

				item.showProgram("close");
				item.lockTiles(); // users cant walk on this tile

				((Player) this.entity).send(new JUMPINGPLACE_OK());
				((Player) this.entity).getRoomUser().setCanWalk(false);

				this.entity.getDetails().setTickets(this.entity.getDetails().getTickets() - 1);
				this.entity.getDetails().sendTickets();
				this.entity.getDetails().save();
			}

			if (definition.getBehaviour().isCanSitOnTop()) {
				this.getPosition().setRotation(item.getPosition().getRotation());
				this.removeStatus("dance");
				this.removeStatus("lay");
				this.setStatus("sit", " " + String.valueOf(this.position.getZ() + definition.getHeight()), true, -1);

				if (this.room.getData().getModelName().equals("hallA")) {
					((Player) this.entity).send(new OPEN_GAMEBOARD("TicTacToe"));
				}

				if (this.room.getData().getModelName().equals("hallB")) {
					((Player) this.entity).send(new OPEN_GAMEBOARD("BattleShip"));
				}

				if (this.room.getData().getModelName().equals("hallD")) {
					((Player) this.entity).send(new OPEN_GAMEBOARD("Poker"));
				}
			}

			if (definition.getBehaviour().isCanLayOnTop()) {

				if (item.isValidPillowTile(position)) {
					this.getPosition().setRotation(item.getPosition().getRotation());
					this.removeStatus("dance");
					this.removeStatus("sit");
					this.removeStatus("carryd");
					this.setStatus("lay", " " + Double.toString(definition.getHeight() + 1.5) + " null", true, -1);
				} else {

					for (Position tile : item.getValidPillowTiles()) {

						if (this.position.getX() != tile.getX()) {
							this.position.setY(tile.getY());
						}

						if (this.position.getY() != tile.getY()) {
							this.position.setX(tile.getX());
						}
					}

					this.currentItemTrigger();
				}
			}

			if (definition.getBehaviour().isTeleporter() && this.entity instanceof Player) {

				Item teleporter = this.room.getItem(item.getTargetTeleporterID());

				if (teleporter == null) {
					teleporter = Roseau.getDao().getItem().getItem(item.getTargetTeleporterID());
				}

				final Player player = (Player) this.entity;
				final Item targetTeleporter = teleporter;
				final Room previousRoom = this.room;
				final Room room = Roseau.getDao().getRoom().getRoom(targetTeleporter.getRoomID(), true);

				if (room != null) {

					this.setCanWalk(false);
					this.room.send(new DOOR_OUT(item, player.getDetails().getName()));

					final Item currentTeleporter = this.current_item;

					Runnable task = new Runnable() {
						@Override
						public void run() {

							if (currentTeleporter.getRoomID() != targetTeleporter.getRoomID()) {

								if (previousRoom != null) {
									previousRoom.leaveRoom(player, false);
								}

								room.loadRoom(player, targetTeleporter.getPosition(), targetTeleporter.getPosition().getRotation());

							} else {
								player.getRoomUser().getPosition().set(targetTeleporter.getPosition());
								player.getRoomUser().sendStatusComposer();
								room.getItem(currentTeleporter.getTargetTeleporterID()).leaveTeleporter(player);
							}
						}
					};

					Roseau.getGame().getScheduler().schedule(task, GameVariables.TELEPORTER_DELAY, TimeUnit.MILLISECONDS);

					return;
				}
			}
		}

		this.needsUpdate = true;

	}


	public boolean walkTo(int x, int y) {

		if (this.room == null) {
			return false;
		}

		if (!this.canWalk) {
			return false;
		}

		if (this.kickWhenStop) {
			this.kickWhenStop = false;
		}

		if (GameVariables.DEBUG_ENABLE) {
			Item item = this.room.getMapping().getHighestItem(x, y);
			if (item != null) {
				Log.println(item.getDefinition().getSprite() + " - " + item.getDefinition().getID() + " - " + item.getCustomData());
			}
		}
		
		this.resetAfkTimer();

		if (!this.room.getMapping().isValidTile(this.entity, x, y)) {
			return false;
		}

		if (this.position.isMatch(new Position(x, y))) {
			return false;
		}

		this.goal.setX(x);
		this.goal.setY(y);

		LinkedList<Position> path = Pathfinder.makePath(this.entity);

		if (path == null) {
			return false;
		}

		if (path.size() == 0) {
			return false;
		}

		this.path = path;
		this.isWalking = true;

		return true;
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

		Runnable task = new Runnable() {
			@Override
			public void run() {
				room.send(new CHAT("CHAT", details.getName(), response));
			}
		};

		Roseau.getGame().getScheduler().schedule(task, GameVariables.BOT_RESPONSE_DELAY, TimeUnit.MILLISECONDS);

	}

	public void lookTowards(Position look) {

		if (this.isWalking) {
			return;
		}

		int diff = this.getPosition().getRotation() - Rotation.calculateHumanDirection(this.position.getX(), this.position.getY(), look.getX(), look.getY());


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

		this.current_item = null;

		this.needsUpdate = false;
		this.isWalking = false;

		this.danceID = 0;
		this.timeUntilNextDrink = -1;
		
		this.resetAfkTimer();

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
		return this.danceID != 0;
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
		return current_item;
	}

	public void setCurrentItem(Item currentitem) {
		this.current_item = currentitem;
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

	public void setAfkTimer(int aFKtime) {
		afkTimer = aFKtime;
	}

	public void resetAfkTimer() {
		afkTimer = GameVariables.AFK_ROOM_KICK;
	}


}
