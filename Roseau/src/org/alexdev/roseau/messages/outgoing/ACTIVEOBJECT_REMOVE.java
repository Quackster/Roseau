package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class ACTIVEOBJECT_REMOVE implements OutgoingMessageComposer {

	private int itemID;

	public ACTIVEOBJECT_REMOVE(int itemID) {
		this.itemID = itemID;
	}

	@Override
	public void write(Response response) {
		response.init("ACTIVEOBJECT_REMOVE");

		String zstring = "00000000000";

		for (int j = 0; j < String.valueOf(this.itemID).length(); j++) {
			for (int i = 0; i < String.valueOf(this.itemID).length(); i++) {
				zstring += "00";
			}
		}

		response.appendNewArgument("");
		response.append(zstring);
		response.append(String.valueOf(this.itemID));
	}
}
