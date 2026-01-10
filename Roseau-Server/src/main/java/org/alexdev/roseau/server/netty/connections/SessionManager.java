package org.alexdev.roseau.server.netty.connections;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.server.netty.NettyPlayerNetwork;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager {
	
	private static final AttributeKey<Player> PLAYER_KEY = AttributeKey.valueOf("player");
	private final ConcurrentMap<Integer, Player> sessions;

	public SessionManager() {
		sessions = new ConcurrentHashMap<>();
	}
	
	public Player addSession(Channel channel) {
		Integer channelId = channel.hashCode();
		
		Player player = new Player(new NettyPlayerNetwork(channel, channelId));
		channel.attr(PLAYER_KEY).set(player);
		
		Roseau.getGame().getPlayerManager().getPlayers().put(channelId, player);
		sessions.putIfAbsent(channelId, player);
		
		return player;
	}

	public void removeSession(Channel channel) {
		try {
			Integer channelId = channel.hashCode();
			Roseau.getGame().getPlayerManager().getPlayers().remove(channelId);
			sessions.remove(channelId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean hasSession(Channel channel) {
		return sessions.containsKey(channel.hashCode());
	}

	public ConcurrentMap<Integer, Player> getSessions() {
		return sessions;
	}
}
