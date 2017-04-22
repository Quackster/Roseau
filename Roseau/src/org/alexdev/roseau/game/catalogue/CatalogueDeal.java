package org.alexdev.roseau.game.catalogue;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.item.Item;

import com.google.common.collect.Lists;

public class CatalogueDeal {
	private String callID;
	private String[] items;
	private int cost;
	
	public CatalogueDeal(String callID, String[] items, int cost) {
		this.callID = callID;
		this.items = items;
		this.cost = cost;
	}

	public String getCallID() {
		return callID;
	}

	public List<CatalogueItem> getItems() {
		
		List<CatalogueItem> items = Lists.newArrayList();
		
		for (String callID : this.items) {
			
			CatalogueItem item = null;
			
			if (callID.contains("|")) {
				item = Roseau.getGame().getCatalogueManager().getItemByCall(callID.split("|")[0]);	
				item.setExtraData(callID.split("|")[1]);	
			} else {
				item = Roseau.getGame().getCatalogueManager().getItemByCall(callID);
			}
		
			items.add(item);
		}
		
		return items;
	}

	public int getCost() {
		return cost;
	}
	
	

}
