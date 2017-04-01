package org.alexdev.roseau.messages.incoming.room.user;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class TALK implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		if (player.getRoomEntity().getRoom() == null) {
			return;
		}
		
		if (reader.getArgumentAmount() < 1) {
			return;
		}
		
		String talkMessage = reader.getMessageBody();
		
		if (!reader.getHeader().equals("CHAT") && !reader.getHeader().equals("SHOUT")  && !reader.getHeader().equals("WHISPER")) {
			return;
		}
		
		player.getRoomEntity().chat(talkMessage, reader.getHeader(), true);
		
	}

}
