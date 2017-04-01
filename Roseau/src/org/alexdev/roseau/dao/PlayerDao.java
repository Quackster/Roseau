package org.alexdev.roseau.dao;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;

public interface PlayerDao {

	public PlayerDetails getDetails(int userId);
	public boolean login(Player player, String username, String password);
	public int getId(String username);

}
