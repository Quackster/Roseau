package org.alexdev.roseau.game.player;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.alexdev.roseau.Roseau;

public class PlayerManager {

	private final ConcurrentHashMap<Integer, Player> players;
	private final List<Permission> permissions;

	public PlayerManager() {
		this.players = new ConcurrentHashMap<>();
		this.permissions = Roseau.getDao().getPlayer().getPermissions();
	}

	public Player getByID(int userId) {
		return players.values().stream()
			.filter(player -> player.getDetails().getId() == userId)
			.findFirst()
			.orElse(null);
	}

	public Player getByIDMainServer(int userId) {
		return players.values().stream()
			.filter(player -> player.getDetails().getId() == userId 
				&& player.getNetwork().getServerPort() == Roseau.getServerPort())
			.findFirst()
			.orElse(null);
	}

	public void syncPlayerTickets(int userId, int tickets) {
		players.values().stream()
			.filter(player -> player.getDetails().getId() == userId)
			.forEach(player -> {
				player.getDetails().setTickets(tickets);
				player.getDetails().sendTickets();
			});
	}

	public Player getPrivateRoomPlayer(int userId) {
		return players.values().stream()
			.filter(player -> player.getDetails().getId() == userId 
				&& player.getNetwork().getServerPort() == Roseau.getPrivateServerPort())
			.findFirst()
			.orElse(null);
	}

	public Player getPrivateRoomPlayer(String username) {
		return players.values().stream()
			.filter(player -> player.getDetails().getName().equalsIgnoreCase(username)
				&& player.getNetwork().getServerPort() == Roseau.getPrivateServerPort())
			.findFirst()
			.orElse(null);
	}

	public Player getPlayerDifferentConnection(int userId, int connectionId) {
		return players.values().stream()
			.filter(player -> player.getDetails().getId() == userId 
				&& player.getNetwork().getConnectionId() != connectionId)
			.findFirst()
			.orElse(null);
	}

	public Player getPlayerByPortDifferentConnection(int userId, int port, int connectionId) {
		return players.values().stream()
			.filter(player -> player.getDetails().getId() == userId 
				&& player.getNetwork().getServerPort() == port 
				&& player.getNetwork().getConnectionId() != connectionId)
			.findFirst()
			.orElse(null);
	}

	public Player getByName(String name) {
		return players.values().stream()
			.filter(player -> player.getDetails().getName().equalsIgnoreCase(name))
			.findFirst()
			.orElse(null);
	}

	public PlayerDetails getPlayerData(int userId) {
		return Optional.ofNullable(getByID(userId))
			.map(Player::getDetails)
			.orElseGet(() -> Roseau.getDao().getPlayer().getDetails(userId));
	}

	public boolean checkForDuplicates(Player player) {
		return Optional.ofNullable(player)
			.filter(p -> p.getNetwork().getConnectionId() != -1)
			.filter(p -> p.getDetails() != null)
			.map(p -> players.values().stream()
				.filter(session -> session.getNetwork().getConnectionId() != -1)
				.filter(session -> session.getDetails() != null)
				.filter(session -> session.getDetails().getId() == p.getDetails().getId())
				.anyMatch(session -> session.getNetwork().getConnectionId() != p.getNetwork().getConnectionId()))
			.orElse(false);
	}

	public List<Player> getMainServerPlayers() {
		return players.values().stream()
			.filter(player -> player.getNetwork().getServerPort() == Roseau.getServerPort())
			.collect(Collectors.toList());
	}

	public boolean hasPermission(int rank, String perm) {
		return permissions.stream()
			.filter(permission -> permission.getPermission().equals(perm))
			.anyMatch(permission -> 
				permission.isInheritable() 
					? rank >= permission.getRank() 
					: rank == permission.getRank());
	}

	public ConcurrentHashMap<Integer, Player> getPlayers() {
		return players;
	}
}
