package org.alexdev.roseau.game;

import org.oldskooler.simplelogger4j.SimpleLog;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.entity.RoomUser;

import java.net.InetAddress;
import java.util.Optional;

public class GameScheduler implements Runnable {
    private static final SimpleLog logger = SimpleLog.of(GameScheduler.class);

	private long tickRate = 0;
	
	@Override
	public void run() {
		try {
			var players = Roseau.getGame().getPlayerManager().getMainServerPlayers();

			if ((this.tickRate % GameVariables.CREDITS_EVERY_SECS) == 0) {
				players.forEach(player -> {
					player.getDetails().setCredits(player.getDetails().getCredits() + GameVariables.CREDITS_EVERY_AMOUNT);
					player.getDetails().sendCredits();
					player.getDetails().save();
				});
			}
			
			if ((this.tickRate % 300) == 0) {
				Optional.of(Roseau.getRawConfigIP())
					.filter(ip -> !Roseau.hasValidIpAddress(ip))
					.ifPresent(ip -> {
						try {
							Roseau.setServerIP(InetAddress.getByName(ip).getHostAddress());
						} catch (Exception e) {
							logger.error("Failed to resolve IP address", e);
						}
					});
			}

			players.stream()
				.map(player -> Optional.ofNullable(player.getPrivateRoomPlayer())
					.or(() -> Optional.ofNullable(player.getPublicRoomPlayer()))
					.orElse(null))
				.filter(roomHandlePlayer -> roomHandlePlayer != null)
				.forEach(roomHandlePlayer -> {
					RoomUser roomUser = roomHandlePlayer.getRoomUser();
					
					if (roomUser.getAfkTimer() > 0) {
						roomUser.setAfkTimer(roomUser.getAfkTimer() - 1);
					} else if (roomUser.getAfkTimer() == 0) {
						roomHandlePlayer.kick();
					}
				});
			
		} catch (Exception e) {
			logger.error("Error in game scheduler", e);
		}
		
		this.tickRate++;
	}
}
