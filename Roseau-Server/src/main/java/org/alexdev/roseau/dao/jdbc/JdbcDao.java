package org.alexdev.roseau.dao.jdbc;

import org.oldskooler.simplelogger4j.SimpleLog;
import org.alexdev.roseau.dao.CatalogueDao;
import org.alexdev.roseau.dao.Dao;
import org.alexdev.roseau.dao.InventoryDao;
import org.alexdev.roseau.dao.ItemDao;
import org.alexdev.roseau.dao.MessengerDao;
import org.alexdev.roseau.dao.NavigatorDao;
import org.alexdev.roseau.dao.PlayerDao;
import org.alexdev.roseau.dao.RoomDao;
import org.alexdev.roseau.util.Util;

public class JdbcDao implements Dao {
    private static final SimpleLog logger = SimpleLog.of(JdbcDao.class);

	private Storage storage;
	private boolean isConnected;

	private PlayerDao player;
	private RoomDao room;
	private ItemDao item;
	private CatalogueDao catalogue;
	private InventoryDao inventory;
	private NavigatorDao navigator;
	private MessengerDao messenger;

	public JdbcDao() {
		this.connect();
		this.player = new JdbcPlayerDao(this);
		this.room = new JdbcRoomDao(this);
		this.item = new JdbcItemDao(this);
		this.catalogue = new JdbcCatalogueDao(this);
		this.inventory = new JdbcInventoryDao(this);
		this.navigator = new JdbcNavigatorDao(this);
		this.messenger = new JdbcMessengerDao(this);
	}

	@Override
	public boolean connect() {
		logger.info("Connecting to database");

		storage = new Storage(Util.getConfiguration().get("Database", "db.hostname", String.class),
				Util.getConfiguration().get("Database", "db.username", String.class),
				Util.getConfiguration().get("Database", "db.password", String.class),
				Util.getConfiguration().get("Database", "db.database", String.class));

		isConnected = storage.isConnected();

		if (!isConnected) {
			logger.error("Could not connect to database");
		} else {
			logger.info("Database connection successful");
		}

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

	@Override
	public NavigatorDao getNavigator() {
		return navigator;
	}

	@Override
	public MessengerDao getMessenger() {
		return messenger;
	}
}
