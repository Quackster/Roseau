package org.alexdev.roseau.messages.incoming;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Bot;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.CHAT;
import org.alexdev.roseau.server.messages.ClientMessage;

public class TALK implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		if (player.getRoomUser().getRoom() == null) {
			return;
		}

		if (reader.getArgumentAmount() < 1) {
			return;
		}

		String talkMessage = reader.getMessageBody();

		if (!reader.getHeader().equals("CHAT") && !reader.getHeader().equals("SHOUT")  && !reader.getHeader().equals("WHISPER")) {
			return;
		}

		if (reader.getHeader().equals("WHISPER")) {

			// Handle whispers
			String[] args = talkMessage.split(" ");

			if (args.length > 1) {

				String username = args[0];
				String message = talkMessage.substring(username.length() + 1);

				Player whispered = Roseau.getGame().getPlayerManager().getByName(username);

				talkMessage = message;

				CHAT response = new CHAT("WHISPER", player.getDetails().getUsername(), message);
				player.send(response);

				if (whispered != null) {
					if (whispered.getDetails().getID() != player.getDetails().getID()) {
						whispered.send(response);
					}
				}
			} else {
				talkMessage =  reader.getMessageBody();
				CHAT response = new CHAT("WHISPER", player.getDetails().getUsername(), reader.getMessageBody());
				player.send(response);
			}
		} else {

			// Handle chat and shout
			talkMessage = reader.getMessageBody();

			List<Player> players = null;

			if (reader.getHeader().equals("SHOUT")) {
				players = player.getRoomUser().getRoom().getPlayers();
			}

			if (reader.getHeader().equals("CHAT")) {
				players = player.getRoomUser().getRoom().getMapping().getNearbyPlayers(player, player.getRoomUser().getPosition(), Roseau.getUtilities().getHabboConfig().get("Player", "talking.lookat.distance", Integer.class));
			}

			CHAT chat = new CHAT(reader.getHeader(), player.getDetails().getUsername(), talkMessage);

			for (Player roomPlayer : players) {
				
				if (roomPlayer != player) {
					roomPlayer.getRoomUser().lookTowards(player.getRoomUser().getPosition());
					roomPlayer.getRoomUser().setLookResetTime(Roseau.getUtilities().getHabboConfig().get("Player", "talking.lookat.reset", Integer.class));
				}
				
				roomPlayer.send(chat);
			}

			if (reader.getHeader().equals("CHAT")) {
				player.send(chat);
			}
		}

		if (reader.getHeader().equals("CHAT") || reader.getHeader().equals("SHOUT")) {

			int talkDuration = 1;

			if (talkMessage.length() > 1) {
				if (talkMessage.length() >= 10) {
					talkDuration = 5;
				} else {
					talkDuration = talkMessage.length() / 2;
				}
			}

			player.getRoomUser().setStatus("talk", "", false, talkDuration, true);

			for (Bot bot : player.getRoomUser().getRoom().getMapping().getNearbyBots(player, player.getRoomUser().getPosition(), 5)) {

				String trigger = bot.containsTrigger(talkMessage);

				if (trigger != null) {
					bot.getRoomUser().chat(bot.getResponse(player.getDetails().getUsername(), trigger), 2);
					player.getRoomUser().setStatus("carryd", " " + trigger, false, 120, true);
				} else {

					if ((talkMessage.toLowerCase().contains("hello") || talkMessage.toLowerCase().contains("hi")) && talkMessage.toLowerCase().contains(bot.getDetails().getUsername().toLowerCase())) {
						bot.getRoomUser().chat("Hello " + player.getDetails().getUsername() + "!", 2);
					}

					if ((talkMessage.toLowerCase().contains("hello") || talkMessage.toLowerCase().contains("hi")) && !talkMessage.toLowerCase().contains(bot.getDetails().getUsername().toLowerCase())) {
						bot.getRoomUser().chat("Hello there!", 2);
					} 
				}
			}
		}
	}

}
