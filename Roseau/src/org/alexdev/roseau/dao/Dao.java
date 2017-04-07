package org.alexdev.roseau.dao;

public interface Dao {

	public boolean connect();
	public PlayerDao getPlayer();
	public boolean isConnected();
	public RoomDao getRoom();
	public ItemDao getItem();
	CatalogueDao getCatalogue();
}
