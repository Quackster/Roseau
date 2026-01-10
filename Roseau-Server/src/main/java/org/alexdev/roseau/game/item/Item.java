package org.alexdev.roseau.game.item;

import java.util.List;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.item.interactors.BlankInteractor;
import org.alexdev.roseau.game.item.interactors.Interaction;
import org.alexdev.roseau.game.item.interactors.TeleporterInteractor;
import org.alexdev.roseau.game.item.interactors.furniture.BedInteractor;
import org.alexdev.roseau.game.item.interactors.furniture.ChairInteractor;
import org.alexdev.roseau.game.item.interactors.pool.PoolChangeBoothInteractor;
import org.alexdev.roseau.game.item.interactors.pool.PoolLadderInteractor;
import org.alexdev.roseau.game.item.interactors.pool.PoolLiftInteractor;
import org.alexdev.roseau.game.item.interactors.pool.PoolQueueInteractor;
import org.alexdev.roseau.game.pathfinder.AffectedTile;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.RoomTile;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.messages.outgoing.ACTIVEOBJECT_UPDATE;
import org.alexdev.roseau.messages.outgoing.SHOWPROGRAM;
import org.alexdev.roseau.messages.outgoing.UPDATEWALLITEM;
import org.alexdev.roseau.server.messages.Response;
import org.alexdev.roseau.server.messages.SerializableObject;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.alexdev.roseau.util.StringUtil;

public class Item implements SerializableObject {
	private int id;
	private int roomId;
	private int targetTeleporterId = 0;
	private Position position;
	private String itemData;
	private String customData;
	private String wallPosition;
	private int definitionId;
	private int ownerId;
	private String currentProgram = null;
	private Interaction interaction;

	public Item(int id, int roomId, int ownerId, String x, int y, double z, int rotation, int definitionId, String itemData, String customData) {
		this.id = id;
		this.roomId = roomId;
		this.ownerId = ownerId;
		this.definitionId = definitionId;

		this.itemData = itemData;	
		this.customData = customData;

		if (this.getDefinition().getBehaviour().isOnWall()) {
			this.wallPosition = x;
			this.position = new Position(-1, -1);
		} else {
			this.position = new Position(Integer.valueOf(x), y, z);
			this.position.setRotation(rotation);
		}
		
		this.setTeleporterId();
		this.setInteractionType();
	}

	private void setInteractionType() {
		if (this.getDefinition().getBehaviour().isCanSitOnTop()) {
			this.interaction = new ChairInteractor(this);
		} else if (this.getDefinition().getBehaviour().isCanLayOnTop()) {
			this.interaction = new BedInteractor(this);
		} else if (this.getDefinition().getBehaviour().isTeleporter()) {
			this.interaction = new TeleporterInteractor(this);
		} else if (this.getDefinition().getSprite().equals("poolBooth")) {
			this.interaction = new PoolChangeBoothInteractor(this);
		} else if (this.getDefinition().getSprite().equals("poolQueue")) {
			this.interaction = new PoolQueueInteractor(this);
		} else if (this.getDefinition().getSprite().equals("poolLift")) {
			this.interaction = new PoolLiftInteractor(this);
		} else if (this.getDefinition().getSprite().equals("poolEnter")) {
			this.interaction = new PoolLadderInteractor(this, true);
		} else if (this.getDefinition().getSprite().equals("poolExit")) {
			this.interaction = new PoolLadderInteractor(this, false);
		}
		
		if (this.interaction == null) {
			this.interaction = new BlankInteractor(this);
		}
	}

	private void setTeleporterId() {
		if (this.getDefinition().getBehaviour().isTeleporter()) {
			try {
				this.targetTeleporterId = Integer.valueOf(this.customData);
			} catch (NumberFormatException e) {  }
		}
	}

	@Override
	public void serialise(Response response) {
		if (this.getDefinition().getBehaviour().isInvisible()) {
			return;
		}

		if (this.getDefinition().getBehaviour().isPassiveObject()) {
			response.appendNewArgument(Integer.toString(this.id));
			response.appendArgument(this.getDefinition().getSprite());
			response.appendArgument(Integer.toString(this.position.getX()));
			response.appendArgument(Integer.toString(this.position.getY()));
			response.appendArgument((int)(this.position.getZ()));
			response.appendArgument(Integer.toString(this.position.getRotation()));
			return;
		}

		if (this.getDefinition().getBehaviour().isOnFloor()) {
			response.appendNewArgument(this.getPadding());
			response.append(Integer.toString(this.getId()));
			response.appendArgument(this.getDefinition().getSprite(), ',');
			response.appendArgument(Integer.toString(this.position.getX()));
			response.appendArgument(Integer.toString(this.position.getY()));
			response.appendArgument(Integer.toString(this.getDefinition().getLength()));
			response.appendArgument(Integer.toString(this.getDefinition().getWidth()));
			response.appendArgument(Integer.toString(this.position.getRotation()));
			response.appendArgument(StringUtil.format(this.position.getZ()));
			response.appendArgument(this.getDefinition().getColor());
			response.appendArgument(this.getDefinition().getName(), '/');
			response.appendArgument(this.getDefinition().getDescription(), '/');

			if (this.targetTeleporterId > 0) {
				response.appendArgument("extr=", '/');
				response.appendArgument(Integer.toString(this.targetTeleporterId), '/');
			}
			
			if (!this.getDefinition().getSprite().equals("fireplace_polyfon")) {

			if (this.customData != null && this.getDefinition().getDataClass() != null) {
				response.appendArgument(this.getDefinition().getDataClass(), '/');
				response.appendArgument(this.customData, '/');
			}
			} else {
				response.appendArgument(this.getDefinition().getDataClass(), '/');
				response.appendArgument(this.customData, '/');				
			}

			return;
		} 

		if (this.getDefinition().getBehaviour().isOnWall()) {
			response.append(Integer.toString(this.id));
			response.appendArgument(this.getDefinition().getSprite(), ';');
			response.appendArgument("Alex", ';');
			response.appendArgument(this.wallPosition, ';');
			response.appendNewArgument(this.customData);
			return;
		}
	}

	public List<Position> getAffectedTiles() {
		return AffectedTile.getAffectedTilesAt(
				this.getDefinition().getLength(), 
				this.getDefinition().getWidth(), 
				this.position.getX(), 
				this.position.getY(),
				this.position.getRotation());
	}

	public boolean canWalk(Entity player, Position position) {
		boolean tile_valid = false;

		if (this.getDefinition().getBehaviour().isCanSitOnTop()) {
			tile_valid = true;
		}

		if (this.getDefinition().getBehaviour().isCanStandOnTop()) {
			tile_valid = true;
		}

		if (this.getDefinition().getBehaviour().isCanLayOnTop()) {
			tile_valid = true;
		}

		if (this.getDefinition().getBehaviour().isTeleporter()) {
			if (this.getDefinition().getDataClass().equals("DOOROPEN")) {
				if (this.customData.equals("TRUE")) {
					tile_valid = true;
				}
			}
		}

		if (this.getDefinition().getSprite().equals("poolBooth")) {
			tile_valid = true;
		}

		if (this.getDefinition().getSprite().equals("stair")) {
		    tile_valid = true;
		}
		
		if (this.getDefinition().getSprite().equals("poolQueue")) {
			tile_valid = true;
		}
		
		if (this.getDefinition().getSprite().equals("poolLift")) {
			tile_valid = player.getDetails().getPoolFigure().length() > 0;
		}

		if (this.getDefinition().getSprite().equals("poolEnter")) {
			tile_valid = player.getDetails().getPoolFigure().length() > 0;
		}

		if (this.getDefinition().getSprite().equals("poolExit")) {
			tile_valid = player.getDetails().getPoolFigure().length() > 0;
		}

		return tile_valid; 
	}


	public RoomTile getTileInstance() {
		return this.getRoom().getMapping().getTile(this.position.getX(), this.position.getY());
	}


	public double getTotalHeight() {
		return this.position.getZ() + this.getDefinition().getHeight();
	}

	public void lockTiles() {
		this.getRoom().getMapping().getTile(this.position.getX(), this.position.getY()).setOverrideLock(true);

		Optional.ofNullable(this.customData)
			.ifPresent(data -> Stream.of(data.split(" "))
				.map(coordinate -> {
					try {
						String[] parts = coordinate.split(",");
						if (parts.length >= 2) {
							int x = Integer.parseInt(parts[0]);
							int y = Integer.parseInt(parts[1]);
							return new int[]{x, y};
						}
						return null;
					} catch (NumberFormatException e) {
						return null;
					}
				})
				.filter(tile -> tile != null)
				.forEach(tile -> this.getRoom().getMapping().getTile(tile[0], tile[1]).setOverrideLock(true))
			);
	}

	public void unlockTiles() {
		this.getRoom().getMapping().getTile(this.position.getX(), this.position.getY()).setOverrideLock(false);

		Optional.ofNullable(this.customData)
			.ifPresent(data -> Stream.of(data.split(" "))
				.map(coordinate -> {
					try {
						String[] parts = coordinate.split(",");
						if (parts.length >= 2) {
							int x = Integer.parseInt(parts[0]);
							int y = Integer.parseInt(parts[1]);
							return new int[]{x, y};
						}
						return null;
					} catch (NumberFormatException e) {
						return null;
					}
				})
				.filter(tile -> tile != null)
				.forEach(tile -> this.getRoom().getMapping().getTile(tile[0], tile[1]).setOverrideLock(false))
			);
	}

	public void updateEntities() {
		Optional.ofNullable(this.getRoom())
			.map(Room::getEntities)
			.ifPresent(entities -> {
				List<Entity> affectedPlayers = entities.stream()
					.filter(entity -> {
						var currentItem = entity.getRoomUser().getCurrentItem();
						
						if (currentItem != null && currentItem.getId() == this.id) {
							if (!hasEntityCollision(entity.getRoomUser().getPosition().getX(), entity.getRoomUser().getPosition().getY())) {
								entity.getRoomUser().setCurrentItem(null);
							}
							return true;
						}
						
						if (hasEntityCollision(entity.getRoomUser().getPosition().getX(), entity.getRoomUser().getPosition().getY())) {
							entity.getRoomUser().setCurrentItem(this);
							return true;
						}
						
						return false;
					})
					.collect(Collectors.toList());
				
				affectedPlayers.forEach(entity -> entity.getRoomUser().currentItemTrigger());
			});
	}

	private boolean hasEntityCollision(int x, int y) {
		if (this.position.getX() == x && this.position.getY() == y) {
			return true;
		}
		
		return this.getAffectedTiles().stream()
			.anyMatch(tile -> tile.getX() == x && tile.getY() == y);
	}

	public OutgoingMessageComposer getCurrentProgram() {
		return Optional.ofNullable(this.currentProgram)
			.map(program -> new SHOWPROGRAM(new String[] { this.itemData, program }))
			.orElse(null);
	}

	
	public void showProgram(String data) {
		if (this.getRoom() == null) {
			return;
		}

		this.currentProgram = data;
		
		OutgoingMessageComposer composer = new SHOWPROGRAM(new String[] { this.itemData, data });
		this.getRoom().send(composer);
	}

	public void updateStatus() {
		if (this.getRoom() == null) {
			return;
		}

		if (this.getDefinition().getBehaviour().isOnFloor()) {
			this.getRoom().send(new ACTIVEOBJECT_UPDATE(this)); 
		} else {
			this.getRoom().send(new UPDATEWALLITEM(this)); 
		}
	}

	public void save() {
		Roseau.getDao().getItem().saveItem(this);
	}

	public void delete() {
		Roseau.getDao().getItem().deleteItem(this.id);
	}

	public String getPadding() {
		String sprite = this.getDefinition().getSprite();
		return String.format("%0" + sprite.length() + "d", 0);
	}

	public int getId() {
		return this.id;
	}

	public ItemDefinition getDefinition() {
		return Roseau.getGame().getItemManager().getDefinition(this.definitionId);
	}

	public Room getRoom() {
		return Roseau.getGame().getRoomManager().getRoomByID(roomId);
	}

	public String getItemData() {
		return itemData;
	}

	public void setItemData(String itemData) {
		this.itemData = itemData;
	}

	public String getCustomData() {
		return customData;
	}

	public void setCustomData(String customData) {
		
		if (customData.length() > 400) {
			customData = customData.substring(0, 400); 
		}
		
		this.customData = customData;
		this.setTeleporterId();
	}

	public String getWallPosition() {
		return wallPosition;
	}

	public void setWallPosition(String wallPosition) {
		this.wallPosition = wallPosition;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public int getTargetTeleporterId() {
		return targetTeleporterId;
	}

	public Interaction getInteraction() {
		return interaction;
	}

	public void setTargetTeleporterId(int targetTeleporterId) {
		this.targetTeleporterId = targetTeleporterId;
	}

	public Position getPosition() {
		return this.position;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
}
