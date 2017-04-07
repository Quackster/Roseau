package org.alexdev.roseau.game.player;

import java.util.concurrent.ConcurrentHashMap;

import org.alexdev.roseau.Roseau;

public class PlayerManager {

	private ConcurrentHashMap<Integer, Player> players;
	
	public PlayerManager() {
		this.players = new ConcurrentHashMap<Integer, Player>();
	}

	public Player getById(int userId) {
		
		try {
			return this.players.values().stream().filter(s -> s.getDetails().getId() == userId).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}
	
	public Player getPrivateRoomPlayer(int userId) {
		
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
	
	public Player getPlayerByIdPort(int userId, int port, int connectionId) {
		
		try {
			return this.players.values().stream().filter(s -> s.getDetails().getId() == userId && s.getNetwork().getServerPort() == port && s.getNetwork().getConnectionId() != connectionId).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public Player getByName(String name) {
		
		try {
			return this.players.values().stream().filter(s -> s.getDetails().getUsername().equals(name)).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}
	
	public PlayerDetails getPlayerData(int userId) {
		
		Player player = this.getById(userId);
		
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
	
	public boolean approveName(String name)
	{
		// FAILproof!
		if (name != null) {
			
			// Atleast 3 characters and not more than 20?
			if (name.length() >= 3 && name.length() <= 20) {
				// Does username start with MOD- ?
				if (name.indexOf("MOD-") != 0) {
					
					// We don't want m0d neither...
					if (name.indexOf("M0D-") != 0)
					{
						// Check for characters
						String allowed = Roseau.getUtilities().getHabboConfig().get("Register", "user.name.chars", String.class);
						
						if (allowed.equals("*")) {
							
							// Any name can pass!
							return true;
						} else {
							
							// Check each character in the name
							char[] nameChars = name.toCharArray();
							
							for (int i = 0; i < nameChars.length; i++) {
								
								// Is this character allowed?
								if (allowed.indexOf(Character.toLowerCase(nameChars[i])) == -1) {
									// Not allowed
									return false;
								}
							}
							
							// Passed all checks!
							return true;
						}
					}
				}
			}
		}
		
		// Bad for whatever reason!
		return false;
	}
	
	public ConcurrentHashMap<Integer, Player> getPlayers() {
		return players;
	}
	
	
}
