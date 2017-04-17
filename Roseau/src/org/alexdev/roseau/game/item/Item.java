package org.alexdev.roseau.game.item;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.pathfinder.AffectedTile;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.RoomTile;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.outgoing.ACTIVEOBJECT_UPDATE;
import org.alexdev.roseau.messages.outgoing.SHOWPROGRAM;
import org.alexdev.roseau.server.messages.Response;
import org.alexdev.roseau.server.messages.SerializableObject;

public class Item implements SerializableObject {

	private int ID;
	private int roomID;
	private int targetTeleporterID = 0;

	private Position position;

	private String itemData;
	private String customData;
	private String wallPosition;

	private ItemDefinition definition;
	private int definitionID;
	private Room room;

	public Item(int ID, int roomID, int ownerID, String x, int y, double z, int rotation, int definitionID, String itemData, String customData) {

		this.ID = ID;
		this.roomID = roomID;

		this.definitionID = definitionID;

		this.itemData = itemData;	
		this.customData = customData;

		this.reload();

		if (this.getDefinition().getBehaviour().isOnWall()) {
			this.wallPosition = x;
		} else {
			//this.x = Integer.valueOf(x);
			this.position = new Position(Integer.valueOf(x), y, z);
			this.position.setBodyRotation(rotation);
		}

		this.setTeleporterID();
	}

	private void reload() {
		this.room = Roseau.getGame().getRoomManager().getRoomByID(roomID);
		this.definition = Roseau.getGame().getItemManager().getDefinition(this.definitionID);
	}

	private void setTeleporterID() {
		if (this.definition.getBehaviour().isTeleporter()) {
			try {
				this.targetTeleporterID = Integer.valueOf(this.customData);
			} catch (NumberFormatException e) {  }
		}
	}

	@Override
	public void serialise(Response response) {

		if (definition.getBehaviour().isInvisible()) {
			return;
		}

		if (definition.getBehaviour().isPassiveObject()) {

			response.appendNewArgument(Integer.toString(this.ID));
			response.appendArgument(definition.getSprite());
			response.appendArgument(Integer.toString(this.position.getX()));
			response.appendArgument(Integer.toString(this.position.getY()));
			response.appendArgument(Integer.toString((int)this.position.getZ()));
			response.appendArgument(Integer.toString(this.position.getRotation()));
		} else {


			if (definition.getBehaviour().isOnFloor()) {
				response.appendNewArgument(this.getPacketID());
				response.appendArgument(definition.getSprite(), ',');
				response.appendArgument(Integer.toString(this.position.getX()));
				response.appendArgument(Integer.toString(this.position.getY()));
				response.appendArgument(Integer.toString(definition.getLength()));
				response.appendArgument(Integer.toString(definition.getWidth()));
				response.appendArgument(Integer.toString(this.position.getRotation()));
				response.appendArgument(Integer.toString((int)this.position.getZ()));
				response.appendArgument(definition.getColor());
				response.appendArgument(definition.getName(), '/');
				response.appendArgument(definition.getDescription(), '/');

		    	/*if (this.targetTeleporterID > 0) {
		    		response.appendArgument("extr=", '/');
		    		response.appendArgument(Integer.toString(this.targetTeleporterID), '/');
		    	}*/
				
				if (this.customData != null && this.definition.getDataClass() != null) {
					response.appendArgument(this.definition.getDataClass(), '/');
					response.appendArgument(this.customData, '/');
				}

			} else if (definition.getBehaviour().isOnWall()) {
				response.appendNewArgument(Integer.toString(this.ID));
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

	public List<Position> getAffectedTiles() {

		ItemDefinition definition = this.getDefinition();

		return AffectedTile.getAffectedTilesAt(
				definition.getLength(), 
				definition.getWidth(), 
				this.position.getX(), 
				this.position.getY(),
				this.position.getRotation());
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

		if (definition.getBehaviour().isTeleporter()) {
			if (definition.getDataClass().equals("DOOROPEN")) {

				if (this.customData.equals("TRUE")) {
					tile_valid = true;
				} else {
					Log.println("invalid XD");
				}
			}
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
		return this.getRoom().getMapping().getTile(this.position.getX(), this.position.getY());
	}

	public void lockTiles() {
		this.getRoom().getMapping().getTile(this.position.getX(), this.position.getY()).setOverrideLock(true);

		try {
			if (this.customData != null) {
				for (String coordinate : this.customData.split(" ")) {
					int x = Integer.valueOf(coordinate.split(",")[0]);
					int y = Integer.valueOf(coordinate.split(",")[1]);

					this.getRoom().getMapping().getTile(x, y).setOverrideLock(true);
				}
			}
		} catch (NumberFormatException e) {	}
	}

	public void unlockTiles() {
		this.getRoom().getMapping().getTile(this.position.getX(), this.position.getY()).setOverrideLock(false);

		try {
			if (this.customData != null) {
				for (String coordinate : this.customData.split(" ")) {
					int x = Integer.valueOf(coordinate.split(",")[0]);
					int y = Integer.valueOf(coordinate.split(",")[1]);

					this.getRoom().getMapping().getTile(x, y).setOverrideLock(false);
				}
			}
		} catch (NumberFormatException e) {	}
	}

	public void showProgram(String data) {

		if (this.room == null) {
			this.reload();
		}

		this.room.send(new SHOWPROGRAM(new String[] { this.itemData, data }));
	}

	public void updateStatus() {

		if (this.room == null) {
			this.reload();
		}

		this.room.send(new ACTIVEOBJECT_UPDATE(this));
	}

	public void save() {
		Roseau.getDataAccess().getItem().saveItem(this);
	}

	public void delete() {
		Roseau.getDataAccess().getItem().deleteItem(this.ID);
	}

	public String getPacketID() {

		String zstring = "00000000000";

		for (int j = 0; j < String.valueOf(this.ID).length(); j++) {
			for (int i = 0; i < String.valueOf(this.ID).length(); i++) {
				zstring += "00";
			}
		}

		return (zstring + this.ID);

	}

	public int getID() {
		return this.ID;
	}

	public ItemDefinition getDefinition() {
		return this.definition;
	}

	public void setItemRotation(int rotation) {

		if (rotation != 0 && rotation != 2 && rotation != 4 && rotation != 6) {
			rotation = 0;
		}

		this.position.setBodyRotation(rotation);
	}

	public Room getRoom() {
		return this.room;
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
		this.setTeleporterID();
	}

	public String getWallPosition() {
		return wallPosition;
	}

	public void setWallPosition(String wallPosition) {
		this.wallPosition = wallPosition;
	}

	public int getRoomID() {
		return roomID;
	}

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}

	public int getTargetTeleporterID() {
		return targetTeleporterID;
	}

	public void setTargetTeleporterID(int targetTeleporterID) {
		this.targetTeleporterID = targetTeleporterID;
	}

	public Position getPosition() {
		return this.position;
	}

}
