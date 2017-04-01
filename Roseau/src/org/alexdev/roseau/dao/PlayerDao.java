package org.alexdev.roseau.dao;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;

public interface PlayerDao {

	public void createPlayer(String username, String password, String email, String mission, String figure, int credits, String sex, String birthday);
	public PlayerDetails getDetails(int userId);
	public boolean login(Player player, String userna, String password);
	public int getId(String username);
	public boolean isNameTaken(String name);
	

}
