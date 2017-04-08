package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.catalogue.CatalogueItem;
import org.alexdev.roseau.game.item.ItemDefinition;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.ORDERINFO;
import org.alexdev.roseau.messages.outgoing.SYSTEMBROADCAST;
import org.alexdev.roseau.server.messages.ClientMessage;

public class GETORDERINFO implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		String callId = reader.getArgument(1);

		CatalogueItem item = Roseau.getGame().getCatalogueManager().getItemByCall(callId);

		if (item == null) {
			return;
		}

		ItemDefinition definition = item.getDefinition();

		if (definition == null) {
			return;
		}

		String extraData = "";

		if (callId.equals("L") || callId.equals("T")) {
			extraData += " " + reader.getArgument(2);
		}

		player.send(new ORDERINFO(item.getCallId() + extraData, item.getCredits()));
	}

}
