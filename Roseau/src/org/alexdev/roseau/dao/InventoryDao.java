package org.alexdev.roseau.dao;

import java.util.List;

import org.alexdev.roseau.game.item.Item;

public interface InventoryDao {

	List<Item> getInventoryItems(int userId);

	Item getItem(long id);

	Item newItem(int itemId, int ownerId, String extraData);

}
