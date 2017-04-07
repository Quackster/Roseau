package org.alexdev.roseau.dao.mysql;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.dao.Dao;
import org.alexdev.roseau.dao.ItemDao;
import org.alexdev.roseau.dao.PlayerDao;
import org.alexdev.roseau.dao.RoomDao;
import org.alexdev.roseau.log.Log;

public class MySQLDao implements Dao {

	private Storage storage;
	private boolean isConnected;

	private PlayerDao player;
	private RoomDao room;
	private ItemDao item;

	public MySQLDao() {

		this.connect();
		this.player = new MySQLPlayerDao(this);
		this.room = new MySQLRoomDao(this);
		this.item = new MySQLItemDao(this);
	}

	@Override
	public boolean connect() {

		Log.println("Connecting to MySQL server");
		
		storage = new Storage(Roseau.getUtilities().getConfiguration().get("Database", "mysql.hostname", String.class), 
				Roseau.getUtilities().getConfiguration().get("Database", "mysql.username", String.class), 
				Roseau.getUtilities().getConfiguration().get("Database", "mysql.password", String.class), 
				Roseau.getUtilities().getConfiguration().get("Database", "mysql.database", String.class)); 

		isConnected = storage.isConnected();

		if (!isConnected) {
			Log.println("Could not connect");
		} else {
			Log.println("Connection to MySQL was a success");
		}

		Log.println();
		
		return isConnected;
	}

	public Storage getStorage() {
		return storage;
	}
	
	@Override
	public boolean isConnected() {
		return isConnected;
	}
		
	@Override
	public PlayerDao getPlayer() {
		return this.player;
	}

	@Override
	public RoomDao getRoom() {
		return room;
	}

	@Override
	public ItemDao getItem() {
		return item;
	}

}
