package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class OPEN_GAMEBOARD extends OutgoingMessageComposer {

	private String gameID;

	public OPEN_GAMEBOARD(String gameID) {
		this.gameID = gameID;
	}

	@Override
	public void write() {
		response.init("OPEN_GAMEBOARD");
		response.appendNewArgument("0");
		response.appendArgument(this.gameID, ';');
	}

}
