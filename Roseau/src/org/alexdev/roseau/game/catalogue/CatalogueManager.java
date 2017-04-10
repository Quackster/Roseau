package org.alexdev.roseau.game.catalogue;

import java.util.Map;

import org.alexdev.roseau.Roseau;

public class CatalogueManager {

	private Map<String, CatalogueItem> items;
	
	public void load() {
		this.items = Roseau.getDataAccess().getCatalogue().getBuyableItems();
	}

	public CatalogueItem getItemByCall(String callID) {
	
		if (this.items.containsKey(callID)) {
			return this.items.get(callID);
		}
		
		return null;
	}
	
	public Map<String, CatalogueItem> getItems() {
		return items;
	}
}
