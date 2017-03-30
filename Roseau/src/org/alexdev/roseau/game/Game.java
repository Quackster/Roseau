package org.alexdev.roseau.game;

import org.alexdev.roseau.dao.Dao;
import org.alexdev.roseau.game.player.PlayerManager;
import org.alexdev.roseau.log.Log;

public class Game {

	PlayerManager playerManager;
	
	public Game(Dao dao) throws Exception {
		this.playerManager = new PlayerManager();
	}
	
	public void load() {
		
		try {

		} catch (Exception e) {
			Log.exception(e);
		}
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
}
