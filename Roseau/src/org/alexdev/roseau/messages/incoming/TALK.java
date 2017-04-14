package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.CHAT_MESSAGE;
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

				CHAT_MESSAGE response = new CHAT_MESSAGE("WHISPER", player.getDetails().getUsername(), message);
				player.send(response);

				if (whispered != null) {
					if (whispered.getDetails().getID() != player.getDetails().getID()) {
						whispered.send(response);
					}
				}
			} else {
				talkMessage =  reader.getMessageBody();
				CHAT_MESSAGE response = new CHAT_MESSAGE("WHISPER", player.getDetails().getUsername(), reader.getMessageBody());
				player.send(response);
			}
		} else {

			// Handle chat and shout
			talkMessage = reader.getMessageBody();

			player.getRoomUser().getRoom().send(new CHAT_MESSAGE(reader.getHeader(), player.getDetails().getUsername(), reader.getMessageBody()));

			if (reader.getHeader().equals("CHAT") || reader.getHeader().equals("SHOUT")) {
				for (Player roomPlayer : player.getRoomUser().getRoom().getMapping().getNearbyPlayers(player, 30)) {
					roomPlayer.getRoomUser().lookTowards(player.getRoomUser().getPosition());
				}
			}
		}

		// Show users mouth moving when chatting
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

		}

		//player.getRoomUser().chat(talkMessage, reader.getHeader(), true);

	}

}
