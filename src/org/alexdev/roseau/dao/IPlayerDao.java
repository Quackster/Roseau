package org.alexdev.roseau.dao;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;

public interface IPlayerDao {

	public PlayerDetails getDetails(int userId);
	public boolean login(Player player, String ssoTicket);
	public int getId(String username);

}
