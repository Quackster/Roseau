package org.alexdev.roseau.game.player;

import java.util.concurrent.ConcurrentHashMap;

import org.alexdev.roseau.Roseau;

public class PlayerManager {

	private ConcurrentHashMap<Integer, Player> players;
	
	public PlayerManager() {
		this.players = new ConcurrentHashMap<Integer, Player>();
	}

	public Player findById(int userId) {
		
		try {
			return this.players.values().stream().filter(s -> s.getDetails().getId() == userId).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}
	
	public Player getIdenticalPlayer(int userId, int connectionId) {
		
		try {
			return this.players.values().stream().filter(s -> s.getDetails().getId() == userId && s.getNetwork().getConnectionId() != connectionId).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public Player findByName(String name) {
		
		try {
			return this.players.values().stream().filter(s -> s.getDetails().getUsername().equals(name)).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}
	
	public PlayerDetails getPlayerData(int userId) {
		
		Player player = this.findById(userId);
		
		if (player == null) {
			return Roseau.getDataAccess().getPlayer().getDetails(userId);
		}
		
		return player.getDetails();
	}
	
	public boolean checkForDuplicates(Player player) {
		
		if (player.getNetwork().getConnectionId() == -1 || player.getDetails() == null) {
			return false;
		}
		
		for (Player session : this.players.values()) {
			
			if (session.getNetwork().getConnectionId() == -1 || session.getDetails() == null) {
				continue;
			}
			
			if (session.getDetails().getId() == player.getDetails().getId()) {
				if (session.getNetwork().getConnectionId() != player.getNetwork().getConnectionId()) { // user tries to login twice
					return true;
				}
			}
		}
		return false;
	}
	
	
	public ConcurrentHashMap<Integer, Player> getPlayers() {
		return players;
	}
}
