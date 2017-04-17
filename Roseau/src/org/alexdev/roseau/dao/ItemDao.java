package org.alexdev.roseau.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.ItemDefinition;

public interface ItemDao {

	public Map<Integer, ItemDefinition> getDefinitions();
	public ConcurrentHashMap<Integer, Item> getPublicRoomItems(String model, int roomID);
	ConcurrentHashMap<Integer, Item> getRoomItems(int roomID);
	void saveItem(Item item);
	void deleteItem(long id);
	Item getItem(int itemID);

}
