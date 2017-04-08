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

		if (callId.equals("L") || callId.equals("T")) {
			
			if (!player.getPrivateRoomPlayer().getRoomUser().getRoom().hasRights(player.getDetails().getId(), false)) {
				player.send(new SYSTEMBROADCAST("Can't buy wallpaper or floor decorations for a room you don't have rights to!"));
				return;
			}
			
			player.send(new ORDERINFO((callId.equals("T") ? "wallpaper" : "floor") + " " + reader.getArgument(2), 2));
			
		} else {

			CatalogueItem item = Roseau.getGame().getCatalogueManager().getItemByCall(callId);

			if (item == null) {
				return;
			}

			ItemDefinition definition = item.getDefinition();

			if (definition == null) {
				return;
			}

			player.send(new ORDERINFO(item.getCallId(), item.getCredits()));
		}

	}

}
