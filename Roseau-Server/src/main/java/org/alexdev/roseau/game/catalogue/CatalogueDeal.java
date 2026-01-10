package org.alexdev.roseau.game.catalogue;

import org.alexdev.roseau.Roseau;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogueDeal {
	private final String callId;
	private final String[] items;
	private final int cost;

	public CatalogueDeal(String callId, String[] items, int cost) {
		this.callId = callId;
		this.items = items;
		this.cost = cost;
	}

	public String getCallId() {
		return callId;
	}

	public List<CatalogueItem> getItems() {
		return Arrays.stream(this.items)
			.map(id -> {
				if (id.contains("|")) {
					String[] parts = id.split("\\|", 2);
					CatalogueItem item = Roseau.getGame().getCatalogueManager().getItemByCall(parts[0]);
					if (item != null && parts.length > 1) {
						item.setExtraData(parts[1]);
					}
					return item;
				} else {
					return Roseau.getGame().getCatalogueManager().getItemByCall(id);
				}
			})
			.collect(Collectors.toList());
	}

	public int getCost() {
		return cost;
	}
}
