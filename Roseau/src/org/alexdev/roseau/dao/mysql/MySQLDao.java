package org.alexdev.roseau.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.dao.CatalogueDao;
import org.alexdev.roseau.dao.Dao;
import org.alexdev.roseau.dao.InventoryDao;
import org.alexdev.roseau.dao.ItemDao;
import org.alexdev.roseau.dao.PlayerDao;
import org.alexdev.roseau.dao.RoomDao;
import org.alexdev.roseau.game.catalogue.CatalogueItem;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.util.BCrypt;

import com.google.common.collect.Maps;

public class MySQLDao implements Dao {

	private Storage storage;
	private boolean isConnected;

	private PlayerDao player;
	private RoomDao room;
	private ItemDao item;
	private CatalogueDao catalogue;
	private InventoryDao inventory;

	public MySQLDao() {

		this.connect();
		this.player = new MySQLPlayerDao(this);
		this.room = new MySQLRoomDao(this);
		this.item = new MySQLItemDao(this);
		this.catalogue = new MySQLCatalogueDao(this);
		this.inventory = new MySQLInventoryDao(this);
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

		//Connection sqlConnection = null;
		//PreparedStatement preparedStatement = null;
		//ResultSet resultSet = null;

		/*try {

			sqlConnection = this.storage.getConnection();
			preparedStatement = this.storage.prepare("SELECT * FROM users", sqlConnection);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				
				Log.println(resultSet.getString("username") + ", " + BCrypt.hashpw(resultSet.getString("password"), BCrypt.gensalt()));

			}
			
			//String hashed = BCrypt.hashpw("123", BCrypt.gensalt(12));
			
			//String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
			
			Log.println("123: " + BCrypt.checkpw("123", "$2a$12$EcU6j0uxhyfoqMfRyEZ67ubiX8l7CbZNnDu1wvoMk7UA5RunmYEU."));

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}*/
		
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

	@Override
	public CatalogueDao getCatalogue() {
		return catalogue;
	}

	@Override
	public InventoryDao getInventory() {
		return inventory;
	}
}
