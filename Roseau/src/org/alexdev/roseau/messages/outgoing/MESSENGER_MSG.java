package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.log.DateTime;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class MESSENGER_MSG implements OutgoingMessageComposer {

	private Player from;
	private String message = "";
	private long timestamp;
	
	public MESSENGER_MSG(Player from, long timestamp) {
		this.from = from;
		this.message = "[placeholder text]";
		this.timestamp = timestamp;
	}
	
	public MESSENGER_MSG(Player from, long timestamp, String message) {
		this.from = from;
		this.timestamp = timestamp;
		this.message = message;
	}

	@Override
	public void write(Response response) {
		response.init("MESSENGER_MSG");
		response.appendNewArgument(String.valueOf("-1"));
		response.appendNewArgument(String.valueOf("1"));
        response.appendNewArgument("[]");
        response.appendNewArgument(DateTime.toString(this.timestamp));
        response.appendNewArgument(this.message);
        response.appendNewArgument(from.getDetails().getFigure());
        response.appendNewArgument("");
	}
}
