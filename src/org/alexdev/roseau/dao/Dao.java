package org.alexdev.roseau.dao;

public interface Dao {

	public boolean connect();
	public IPlayerDao getPlayer();
	public boolean isConnected();
}
