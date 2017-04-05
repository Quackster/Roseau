package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.log.Log;
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
			String[] args = talkMessage.split(" ");

			if (args.length > 1) {

				String username = args[0];
				String message = talkMessage.substring(username.length() + 1);

				Player whispered = Roseau.getGame().getPlayerManager().getByName(username);

				CHAT_MESSAGE response = new CHAT_MESSAGE("WHISPER", player.getDetails().getUsername(), message);
				player.send(response);

				if (whispered != null) {
					if (whispered.getDetails().getId() != player.getDetails().getId()) {
						whispered.send(response);
					}
				}
			} else {
				CHAT_MESSAGE response = new CHAT_MESSAGE("WHISPER", player.getDetails().getUsername(), reader.getMessageBody());
				player.send(response);
			}
		} else {
			
			player.getRoomUser().getRoom().send(new CHAT_MESSAGE(reader.getHeader(), player.getDetails().getUsername(), reader.getMessageBody()));
			
			for (Player roomPlayer : player.getRoomUser().getRoom().getPlayers()) {
				if (roomPlayer == player) {
					continue;
				}
				
				Position currentPoint = player.getRoomUser().getPosition();
				Position playerPoint = roomPlayer.getRoomUser().getPosition();
				
				Log.println("Chat distance: " + currentPoint.getDistance(playerPoint));
				
				if (currentPoint.getDistance(playerPoint) <= 30) {
					roomPlayer.getRoomUser().lookTowards(playerPoint);
				}
			}
		}

		//player.getRoomUser().chat(talkMessage, reader.getHeader(), true);

	}

}
