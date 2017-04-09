package org.alexdev.roseau.game.room;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.outgoing.ACTIVEOBJECT_ADD;
import org.alexdev.roseau.messages.outgoing.ACTIVEOBJECT_UPDATE;
import org.alexdev.roseau.messages.outgoing.ACTIVE_OBJECTS;
import org.alexdev.roseau.messages.outgoing.ITEMS;

public class RoomMapping {

	private Room room;
	private RoomTile[][] tiles;

	public RoomMapping(Room room) {
		this.room = room;

	}

	public void regenerateCollisionMaps() {

		int mapSizeX = this.room.getData().getModel().getMapSizeX();
		int mapSizeY = this.room.getData().getModel().getMapSizeY();

		this.tiles = new RoomTile[mapSizeX][mapSizeY];

		for (int y = 0; y < mapSizeY; y++) {
			for (int x = 0; x < mapSizeX; x++) {
				this.tiles[x][y] = new RoomTile(this.room);
				this.tiles[x][y].setHeight(this.room.getData().getModel().getHeight(x, y));
			}
		}

		ConcurrentHashMap<Integer, Item> items;

		if (room.getData().getRoomType() == RoomType.PUBLIC) { 
			items = room.getPassiveObjects();
		} else {
			items = room.getItems();
		}

		for (Item item : items.values()) {

			if (item == null) {
				continue;
			}

			double stacked_height = 0;

			if (item.getDefinition().getBehaviour().isCanStackOnTop()) {
				stacked_height = item.getDefinition().getHeight();
			}

			this.checkHighestItem(item, item.getX(), item.getY());

			RoomTile roomTile = this.getTile(item.getX(), item.getY());
			
			roomTile.getItems().add(item);
			roomTile.setHeight(roomTile.getHeight() + stacked_height);

			for (Position tile : item.getAffectedTiles()) {

				this.checkHighestItem(item, tile.getX(), tile.getY());

				RoomTile affectedRoomTile = this.getTile(tile.getX(), tile.getY());
				
				affectedRoomTile.getItems().add(item);
				affectedRoomTile.setHeight(affectedRoomTile.getHeight() + stacked_height);
			}
		}
	}

	private void checkHighestItem(Item item, int x, int y) {
		Item highest_item = this.tiles[x][y].getHighestItem();

		if (highest_item == null) {
			this.tiles[x][y].setHighestItem(item);
		}
		else {
			if (item.getZ() > highest_item.getZ()) {
				this.tiles[x][y].setHighestItem(item);
			}
		}
	}

	public boolean isValidTile(Entity entity, int x, int y) {

		RoomTile tile = this.tiles[x][y];

		if (tile.hasOverrideLock()) {
			return false;
		}

		Item item = tile.getHighestItem();
		boolean tile_valid = (this.room.getData().getModel().isBlocked(x, y) == false);

		if (item != null) {
			tile_valid = item.canWalk(entity);
		}

		// This is returned when there's no items found, it will
		// just check the default model if the tile is valid
		return tile_valid;
	}

	public void addItem(Item item, boolean wall_item) {

		item.setRoomId(this.room.getData().getId());
		//item->extra_data = "";

		this.room.getItems().put(item.getId(), item);

		if (!wall_item) {
			if (item.getDefinition().getBehaviour().isOnFloor()) {
				this.handleItemAdjustment(item, false);
				this.regenerateCollisionMaps();
			}
		}
		
		this.room.send(new ACTIVEOBJECT_ADD(item));
		item.save();
	}

	public void updateItemPosition(Item item, boolean rotation_only) {

		if (item.getDefinition().getBehaviour().isOnFloor()) {
			this.handleItemAdjustment(item, rotation_only);
			this.regenerateCollisionMaps();
		}

	    item.updateStatus();	    
		item.save();
	}


	private void handleItemAdjustment(Item item, boolean rotation_only) {
		
	    if (rotation_only) {
	        for (Item items : this.getTile(item.getX(), item.getY()).getItems()) {
	            if (items != item && items.getZ() >= item.getZ()) {
	                items.setRotation(item.getRotation());
	                items.updateStatus();
	            }
	        }
	    }
	    else {
	        item.setZ(this.getStackHeight(item.getX(), item.getY()));
	    }

	}

	private double getStackHeight(int x, int y) {
		return this.tiles[x][y].getHeight();
	}

	public RoomTile getTile(int x, int y) {
		return this.tiles[x][y];
	}

	public Item getHighestItem(int x, int y) {
		return this.tiles[x][y].getHighestItem();
	}

}
