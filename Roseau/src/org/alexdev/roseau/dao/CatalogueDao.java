package org.alexdev.roseau.dao;

import java.util.List;
import java.util.Map;

import org.alexdev.roseau.game.catalogue.CatalogueItem;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.ItemDefinition;

public interface CatalogueDao {

	public Map<String, CatalogueItem> getBuyableItems();

}
