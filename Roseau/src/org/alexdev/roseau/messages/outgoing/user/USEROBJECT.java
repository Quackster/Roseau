package org.alexdev.roseau.messages.outgoing.user;

import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class USEROBJECT implements OutgoingMessageComposer {

	private PlayerDetails details;

	public USEROBJECT(PlayerDetails details) {
		this.details = details;
	}

	@Override
	public void write(Response response) {
		response.init("USEROBJECT");
		response.appendObject(this.details);
	}

}
