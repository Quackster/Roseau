package org.alexdev.roseau.game.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.outgoing.STRIPINFO;

public class Inventory {

	private boolean initalised;
	private Player player;

	private List<Item> items;
	private Map<Integer, List<Item>> paginatedItems;
	
	private int cursor;

	public Inventory(Player player) {
		
		this.initalised = false;
		this.player = player;
		
		this.paginatedItems = new HashMap<Integer, List<Item>>();
		this.cursor = 0;
	}

	public void load() {
		
		this.dispose();

		this.items = Roseau.getDataAccess().getInventory().getInventoryItems(this.player.getDetails().getId());
		this.refreshPagination();
	}

	public void refreshPagination() {

		Log.println("items: " + this.items.size());
		
		this.paginatedItems.clear();

		int pageID = 0;
		int counter = 0;
	

		for (Item item : this.items) {

			if (counter > 6) {
				pageID++;
				counter = 0;
			} else {
				counter++;
			}

			if (!this.paginatedItems.containsKey(pageID)) {
				this.paginatedItems.put(pageID, new ArrayList<Item>());
			}
			
			this.paginatedItems.get(pageID).add(item);
		}
	}


	public Item getItem(int id) {

		Optional<Item> inventoryItem = this.items.stream().filter(item -> item.getId() == id).findFirst();

		if (inventoryItem.isPresent()) {
			return inventoryItem.get();
		} else {
			return null;
		}
	}

	public void removeItem(Item item) {

		if (item != null) {
			this.items.remove(item);
		}

		this.refreshPagination();
	}

	public void addItem(Item item) {

		if (item != null) {
			this.items.add(item);
		}

		this.refreshPagination();
	}

	public void refresh(String mode) {
		
		if (this.paginatedItems.size() > 0) {
			
			if (mode.equals("last")) {
				cursor = this.paginatedItems.size() - 1;
			}
			
			if (mode.equals("new")) {
				cursor = 0;
			}
			
			if (mode.equals("next")) {
				cursor++;
			}
	
			if (this.paginatedItems.containsKey(this.cursor)) {
				this.player.send(new STRIPINFO(this.paginatedItems.get(this.cursor)));
			} else {
				this.cursor = 0;
			}
		}
	}

	public void dispose() {

		if (!this.initalised) {
			return;
		}

		if (this.items != null) {
			this.items.clear();
			this.items = null;
		}
	}

	public boolean isInitalised() {
		return initalised;
	}

	public void setInitalised(boolean initalised) {
		this.initalised = initalised;
	}

	public List<Item> getItems() {
		return items;
	}

	public int getCursor() {
		return cursor;
	}

	public void setCursor(int cursor) {
		this.cursor = cursor;
	}
}
