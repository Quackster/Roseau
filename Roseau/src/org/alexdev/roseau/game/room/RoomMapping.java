package org.alexdev.roseau.game.room;

import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.pathfinder.AffectedTile;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.outgoing.ACTIVEOBJECT_UPDATE;
import org.alexdev.roseau.messages.outgoing.ACTIVE_OBJECTS;

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

		for (Item item : this.room.getItems().values()) {

			if (item == null) {
				Log.println("lol????");
				continue;
			}

			double stacked_height = 0;

			if (item.getDefinition().getBehaviour().isCanStackOnTop()) {
				stacked_height = item.getDefinition().getHeight();
			}

			this.checkHighestItem(item, item.getX(), item.getY());

			this.tiles[item.getX()][item.getY()].setHeight(this.tiles[item.getX()][item.getY()].getHeight() + stacked_height);
			this.tiles[item.getX()][item.getY()].getItems().add(item);

			for (Position tile : item.getAffectedTiles()) {

				this.checkHighestItem(item, tile.getX(), tile.getY());
				
				this.tiles[tile.getX()][tile.getY()].getItems().add(item);
				this.tiles[tile.getX()][tile.getY()].setHeight(this.tiles[tile.getX()][tile.getY()].getHeight() + stacked_height);
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
	
	public void addItem(Item item) {

	    item.setRoomId(this.room.getData().getId());
	    //item->extra_data = "";

	    this.room.getItems().put(item.getId(), item);

	    if (item.getDefinition().getBehaviour().isOnFloor()) {
	        this.handleItemAdjustment(item);
	        this.regenerateCollisionMaps();
	    }

	    this.room.send(new ACTIVE_OBJECTS(this.room));
	    item.save();
	}
	
	public void updateItemPosition(Item item) {

	    if (item.getDefinition().getBehaviour().isOnFloor()) {
	        this.handleItemAdjustment(item);
	        this.regenerateCollisionMaps();
	    }

	    this.room.send(new ACTIVEOBJECT_UPDATE(item));
	    item.save();
	}

	
	private void handleItemAdjustment(Item item) {
		// TODO Auto-generated method stub
		
	}

	public RoomTile getTile(int x, int y) {
		return this.tiles[x][y];
	}

	public Item getHighestItem(int x, int y) {
		return this.tiles[x][y].getHighestItem();
	}

}
