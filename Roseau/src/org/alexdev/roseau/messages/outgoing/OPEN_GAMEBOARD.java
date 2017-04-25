package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class OPEN_GAMEBOARD implements OutgoingMessageComposer {

	private String gameID;

	public OPEN_GAMEBOARD(String gameID) {
		this.gameID = gameID;
	}

	@Override
	public void write(Response response) {
		response.init("OPEN_GAMEBOARD");
		response.appendNewArgument("gameId");
		response.appendArgument(this.gameID, ';');
	}

}
