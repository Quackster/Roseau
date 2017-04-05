package org.alexdev.roseau.game.item;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.pathfinder.AffectedTile;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.RoomTile;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.messages.outgoing.SHOWPROGRAM;
import org.alexdev.roseau.server.messages.Response;
import org.alexdev.roseau.server.messages.SerializableObject;

public class Item implements SerializableObject {

	private int id;
	private String sprite;
	private int x;
	private int y;
	private int definition;
	private double z;
	private int rotation;
	private Room room;
	private String itemData;
	private String customData;

	public Item(int id, int roomId, String sprite, int x, int y, double z, int rotation, int definition, String itemData, String customData) {
		this.id = id;
		this.sprite = sprite;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotation = rotation;
		this.definition = definition;
		this.room = Roseau.getGame().getRoomManager().getRoomById(roomId);
		this.itemData = itemData;
		this.customData = customData;
	}

	@Override
	public void serialise(Response response) {

		ItemDefinition definition = this.getDefinition();
		
		if (definition.getBehaviour().isInvisible()) {
			return;
		}

		//if (definition.getBehaviour().isOnFloor()) {
		if (definition.getBehaviour().isPassiveObject()) {

			response.appendNewArgument(Integer.toString(this.id));
			response.appendArgument(definition.getSprite());
			response.appendArgument(Integer.toString(this.x));
			response.appendArgument(Integer.toString(this.y));
			response.appendArgument(Integer.toString((int)this.z));
			response.appendArgument(Integer.toString(this.rotation));
		} else {
			response.appendNewArgument(Integer.toString(this.id));
			response.appendArgument(definition.getSprite(), ',');
			response.appendArgument(Integer.toString(this.x));
			response.appendArgument(Integer.toString(this.y));
			response.appendArgument(Integer.toString(definition.getLength()));
			response.appendArgument(Integer.toString(definition.getWidth()));
			response.appendArgument(Integer.toString(this.rotation));
			response.appendArgument(Integer.toString((int)this.z));
			response.appendArgument(definition.getColor());

			response.appendArgument(definition.getName(), '/');
			response.appendArgument(definition.getDescription(), '/');

			if (this.customData != null) {
				response.appendArgument(definition.getDataClass(), '/');
				response.appendArgument(this.customData, '/');
			}
		}
		//}
	}
	
	public void showProgram(String data) {
		this.getRoom().send(new SHOWPROGRAM(this.itemData, data));
	}

	public List<AffectedTile> getAffectedTiles() {
		ItemDefinition definition = this.getDefinition();
		return AffectedTile.getAffectedTilesAt(definition.getLength(), definition.getWidth(), this.x, this.y, this.rotation);
	}

	public boolean canWalk() {

		ItemDefinition definition = this.getDefinition();

		boolean tile_valid = false;

		if (definition.getBehaviour().isCanSitOnTop()) {
			tile_valid = true;
		}

		if (definition.getBehaviour().isCanLayOnTop()) {
			tile_valid = true;
		}

		if (definition.getBehaviour().isCanStandOnTop()) {
			tile_valid = true;
		}

		if (definition.getSprite().equals("poolBooth")) {
			tile_valid = true;
		}

		if (definition.getSprite().equals("poolEnter")) {
			tile_valid = true;
		}
		
		for (Player player : this.getRoom().getUsers()) {
			if (player.getRoomUser().getPosition().sameAs(new Position(this.x, this.y))) {
				tile_valid = false;
			}
		}
		

		return tile_valid; 
	}
	
	public RoomTile getTileInstance() {
		return this.getRoom().getMapping().getTile(this.x, this.y);
	}
	
	public void lockTiles() {
		this.getRoom().getMapping().getTile(this.x, this.y).setOverrideLock(true);
		
		if (this.customData != null) {
			for (String coordinate : this.customData.split(" ")) {
				int x = Integer.valueOf(coordinate.split(",")[0]);
				int y = Integer.valueOf(coordinate.split(",")[1]);
				
				this.getRoom().getMapping().getTile(x, y).setOverrideLock(true);
			}
		}
	}
	
	public void unlockTiles() {
		this.getRoom().getMapping().getTile(this.x, this.y).setOverrideLock(false);
		
		if (this.customData != null) {
			for (String coordinate : this.customData.split(" ")) {
				int x = Integer.valueOf(coordinate.split(",")[0]);
				int y = Integer.valueOf(coordinate.split(",")[1]);
				
				this.getRoom().getMapping().getTile(x, y).setOverrideLock(false);
			}
		}
	}

	public int getId() {
		return id;
	}

	public String getSprite() {
		return sprite;
	}


	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public ItemDefinition getDefinition() {
		return Roseau.getGame().getItemManager().getDefinition(this.definition);
	}

	public double getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public int getDefinitionId() {
		return this.definition;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
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
		this.customData = customData;
	}


}
