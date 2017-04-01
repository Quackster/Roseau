package org.alexdev.roseau.game.item;

import org.alexdev.roseau.Roseau;
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
	
	public Item(int id, String sprite, int x, int y, double z, int rotation, int definition) {
		this.id = id;
		this.sprite = sprite;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotation = rotation;
		this.definition = definition;
	}
	
	@Override
	public void serialise(Response response) {
		
		ItemDefinition definition = this.getDefinition();
		
		if (definition.getBehaviour().isPassiveObject()) {
			
			response.appendNewArgument(Integer.toString(this.id));
			response.appendArgument(definition.getSprite());
			response.appendArgument(Integer.toString(this.x));
			response.appendArgument(Integer.toString(this.y));
			response.appendArgument(Integer.toString((int)this.z));
			response.appendArgument(Integer.toString(this.rotation));
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




}
