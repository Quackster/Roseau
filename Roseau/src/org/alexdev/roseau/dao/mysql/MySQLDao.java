package org.alexdev.roseau.dao.mysql;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.dao.Dao;
import org.alexdev.roseau.dao.IPlayerDao;
import org.alexdev.roseau.dao.IRoomDao;
import org.alexdev.roseau.log.Log;

public class MySQLDao implements Dao {

	private IPlayerDao player;
	private Storage storage;
	private boolean isConnected;
	private IRoomDao room;

	public MySQLDao() {

		this.connect();
		this.player = new MySQLPlayerDao(this);
		this.room = new MySQLRoomDao(this);
	}

	@Override
	public boolean connect() {

		Log.println("Connecting to MySQL server");

		storage = new Storage(Roseau.getUtilities().getConfiguration().get("mysql-hostname"), 
				Roseau.getUtilities().getConfiguration().get("mysql-username"), 
				Roseau.getUtilities().getConfiguration().get("mysql-password"), 
				Roseau.getUtilities().getConfiguration().get("mysql-database")); 


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
	public IPlayerDao getPlayer() {
		return this.player;
	}

	@Override
	public IRoomDao getRoom() {
		return room;
	}

}
