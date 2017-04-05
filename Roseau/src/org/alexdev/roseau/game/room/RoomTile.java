package org.alexdev.roseau.game.room;

import java.util.ArrayList;
import java.util.List;

import org.alexdev.roseau.game.item.Item;

public class RoomTile {

	private Room room;
	private double height = 0;
	private List<Item> items;
	private Item highestItem = null;
	private boolean overrideLock = false;
	
	public RoomTile(Room room) {
		this.room = room;
		this.items = new ArrayList<Item>();
	}
	
	public Room getRoom() {
		return room;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
	
	public List<Item> getItems() {
		return items;
	}

	public Item getHighestItem() {
		return highestItem;
	}

	public void setHighestItem(Item highestItem) {
		this.highestItem = highestItem;
	}

	public boolean hasOverrideLock() {
		return overrideLock;
	}

	public void setOverrideLock(boolean overrideLock) {
		this.overrideLock = overrideLock;
	}

}
