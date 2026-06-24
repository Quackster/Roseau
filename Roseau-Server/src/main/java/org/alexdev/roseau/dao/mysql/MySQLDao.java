package org.alexdev.roseau.dao.mysql;

import org.alexdev.roseau.dao.CatalogueDao;
import org.alexdev.roseau.dao.Dao;
import org.alexdev.roseau.dao.InventoryDao;
import org.alexdev.roseau.dao.ItemDao;
import org.alexdev.roseau.dao.MessengerDao;
import org.alexdev.roseau.dao.NavigatorDao;
import org.alexdev.roseau.dao.PlayerDao;
import org.alexdev.roseau.dao.RoomDao;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.util.Util;

public class MySQLDao implements Dao {

	private Storage storage;
	private boolean isConnected;

	private PlayerDao player;
	private RoomDao room;
	private ItemDao item;
	private CatalogueDao catalogue;
	private InventoryDao inventory;
	private NavigatorDao navigator;
	private MessengerDao messenger;

	public MySQLDao() {

		this.connect();
		this.player = new MySQLPlayerDao(this);
		this.room = new MySQLRoomDao(this);
		this.item = new MySQLItemDao(this);
		this.catalogue = new MySQLCatalogueDao(this);
		this.inventory = new MySQLInventoryDao(this);
		this.navigator = new MySQLNavigatorDao(this);
		this.messenger = new MySQLMessengerDao(this);
	}

	@Override
	public boolean connect() {
		DatabaseEngine engine = DatabaseEngine.from(Util.getConfiguration().get("Database", "type", String.class));
		String prefix = engine.getConfigPrefix();

		Log.println("Connecting to " + prefix + " database");

		storage = new Storage(
				engine,
				this.getDatabaseSetting(engine, "hostname", "127.0.0.1"),
				this.getDatabasePort(engine, "port", engine.getDefaultPort()),
				this.getDatabaseSetting(engine, "username", ""),
				this.getDatabaseSetting(engine, "password", ""),
				this.getDatabaseSetting(engine, "database", "roseau"),
				this.getDatabaseSetting(engine, "path", "roseau.sqlite"),
				this.getDatabaseSetting(engine, "options", ""));

		isConnected = storage.isConnected();

		if (!isConnected) {
			Log.println("Could not connect");
		} else {
			Log.println("Connection to " + prefix + " was a success");
		}
		
		Log.println();
		
		return isConnected;
	}

	public Storage getStorage() {
		return storage;
	}

	private String getDatabaseSetting(DatabaseEngine engine, String key, String defaultValue) {
		String value = Util.getConfiguration().get("Database", key);

		if (value == null || value.trim().isEmpty()) {
			value = Util.getConfiguration().get("Database", engine.getConfigPrefix() + "." + key);
		}

		if (value == null || value.trim().isEmpty()) {
			return defaultValue;
		}

		return value.trim();
	}

	private int getDatabasePort(DatabaseEngine engine, String key, int defaultValue) {
		String value = this.getDatabaseSetting(engine, key, "");

		if (value.isEmpty()) {
			return defaultValue;
		}

		return Integer.parseInt(value);
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

	@Override
	public NavigatorDao getNavigator() {
		return navigator;
	}

	@Override
	public MessengerDao getMessenger() {
		return messenger;
	}
}
