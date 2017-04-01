package org.alexdev.roseau.dao;

import java.util.List;
import java.util.Map;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.ItemDefinition;

public interface ItemDao {

	public Map<Integer, ItemDefinition> getDefinitions();
	public List<Item> getPublicRoomItems(String model);

}
