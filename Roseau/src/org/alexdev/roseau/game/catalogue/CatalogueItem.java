package org.alexdev.roseau.game.catalogue;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.item.ItemDefinition;

public class CatalogueItem {

	private int credits;
	private int definitionId;
	private String callId;
	
	public CatalogueItem(String callId, int definitionId, int credits) {
		this.callId = callId;
		this.credits = credits;
		this.definitionId = definitionId;
	}

	public String getCallId() {
		return callId;
	}

	public int getCredits() {
		return credits;
	}

	public ItemDefinition getDefinition() {
		return Roseau.getGame().getItemManager().getDefinition(this.definitionId);
	}
}
