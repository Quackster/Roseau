package org.alexdev.roseau.game.inventory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;

public class Inventory {

	private boolean initalised;
	private Player player;
	private List<Item> items;

	public Inventory(Player player) {
		this.initalised = false;
		this.player = player;
	}
	
	public void load() {
			
			// Clear any previous inventory items, etc
			this.dispose();
			
			// Reload new inventory
			this.items = Roseau.getDataAccess().getInventory().getInventoryItems(this.player.getDetails().getId());
	}
	

	public Item getItem(int id) {
		
		Optional<Item> inventoryItem = this.items.stream().filter(item -> item.getId() == id).findFirst();
		
		if (inventoryItem.isPresent()) {
			return inventoryItem.get();
		} else {
			return null;
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
}
