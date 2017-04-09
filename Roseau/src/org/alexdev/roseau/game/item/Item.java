package org.alexdev.roseau.game.item;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.pathfinder.AffectedTile;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.RoomTile;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.messages.outgoing.SHOWPROGRAM;
import org.alexdev.roseau.server.messages.Response;
import org.alexdev.roseau.server.messages.SerializableObject;

public class Item implements SerializableObject {

	private int id;
	private int x;
	private int y;
	private int definition;
	private double z;
	private int rotation;
	private int room;
	private String itemData;
	private String customData;

	private String wallPosition;

	public Item(int id, int roomId, int ownerId, String x, int y, double z, int rotation, int definition, String itemData, String customData) {

		this.definition = definition;

		if (this.getDefinition().getBehaviour().isOnWall()) {
			this.wallPosition = x;
		} else {
			this.x = Integer.valueOf(x);
		}

		this.id = id;
		this.y = y;
		this.z = z;
		this.rotation = rotation;

		this.room = roomId;
		this.itemData = itemData;
		this.customData = customData;
	}

	@Override
	public void serialise(Response response) {

		ItemDefinition definition = this.getDefinition();

		if (definition.getBehaviour().isInvisible()) {
			return;
		}

		if (definition.getBehaviour().isPassiveObject()) {

			response.appendNewArgument(Integer.toString(this.id));
			response.appendArgument(definition.getSprite());
			response.appendArgument(Integer.toString(this.x));
			response.appendArgument(Integer.toString(this.y));
			response.appendArgument(Integer.toString((int)this.z));
			response.appendArgument(Integer.toString(this.rotation));
		} else {


			if (definition.getBehaviour().isOnFloor()) {

				int zero = this.id;
				String zstring = "00000000000";

				for (int j = 0; j < String.valueOf(zero).length(); j++) {
					for (int i = 0; i < String.valueOf(zero).length(); i++) {
						zstring += "00";
					}
				}

				response.appendNewArgument("");
				response.append(zstring);
				response.append(Integer.toString(this.id));
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
					response.appendArgument(this.customData, '/');
				}

			} else if (definition.getBehaviour().isOnWall()) {
				response.appendNewArgument(Integer.toString(this.id));
				response.appendTabArgument(definition.getSprite());
				response.appendTabArgument(" ");
				response.appendTabArgument(this.wallPosition);

				if (this.customData != null) {
					response.appendTabArgument(this.customData);

				} else {
					response.appendTabArgument("");
				}
			}
		}
	}

	public void showProgram(String data) {
		this.getRoom().send(new SHOWPROGRAM(this.itemData, data));
	}

	public List<Position> getAffectedTiles() {

		ItemDefinition definition = this.getDefinition();

		return AffectedTile.getAffectedTilesAt(
				definition.getLength(), 
				definition.getWidth(), 
				this.x, 
				this.y,
				this.rotation);
	}

	public boolean canWalk(Entity player) {

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
			tile_valid = player.getDetails().getPoolFigure().length() > 0;
		}

		if (definition.getSprite().equals("poolExit")) {
			tile_valid = player.getDetails().getPoolFigure().length() > 0;
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

	public void save() {
		Roseau.getDataAccess().getItem().saveItem(this);
	}

	public void delete() {
		Roseau.getDataAccess().getItem().deleteItem(this.id);
	}

	public int getId() {
		return id;
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

	public void setZ(double d) {
		this.z = d;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		
		if (rotation == 1) {
			rotation = 0;
		}
		
		if (rotation == 3) {
			rotation = 2;
		}
		
		if (rotation == 5) {
			rotation = 4;
		}
		
		if (rotation == 7) {
			rotation = 6;
		}
		
		this.rotation = rotation;
	}

	public int getDefinitionId() {
		return this.definition;
	}

	public Room getRoom() {
		return Roseau.getGame().getRoomManager().getRoomById(this.room);
	}

	public int getRoomId() {
		return room;
	}

	public void setRoomId(int room) {
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

	public String getWallPosition() {
		return wallPosition;
	}

	public void setWallPosition(String wallPosition) {
		this.wallPosition = wallPosition;
	}

}
