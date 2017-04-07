package org.alexdev.roseau.game;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.alexdev.roseau.dao.Dao;
import org.alexdev.roseau.game.catalogue.CatalogueManager;
import org.alexdev.roseau.game.item.ItemManager;
import org.alexdev.roseau.game.player.PlayerManager;
import org.alexdev.roseau.game.room.RoomManager;
import org.alexdev.roseau.log.Log;

public class Game {

	private PlayerManager playerManager;
	private RoomManager roomManager;
	private ItemManager itemManager;
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);
	private CatalogueManager catalogueManager;
	
	public Game(Dao dao) throws Exception {
		this.playerManager = new PlayerManager();
		this.roomManager = new RoomManager();
		this.itemManager = new ItemManager();
		this.catalogueManager = new CatalogueManager();
	}
	
	public void load() {
		
		try {
			this.roomManager.load();
			this.itemManager.load();
			this.catalogueManager.load();
		} catch (Exception e) {
			Log.exception(e);
		}
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public RoomManager getRoomManager() {
		return roomManager;
	}

	public ScheduledExecutorService getScheduler() {
		return scheduler;
	}

	public ItemManager getItemManager() {
		return itemManager;
	}

	public CatalogueManager getCatalogueManager() {
		return catalogueManager;
	}
}
