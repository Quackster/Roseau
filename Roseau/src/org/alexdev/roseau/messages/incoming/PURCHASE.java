package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.catalogue.CatalogueItem;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.ADDSTRIPITEM;
import org.alexdev.roseau.messages.outgoing.SYSTEMBROADCAST;
import org.alexdev.roseau.server.messages.ClientMessage;

public class PURCHASE implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		String callId = reader.getArgument(0).replace("/", "");
		
		CatalogueItem product = Roseau.getGame().getCatalogueManager().getItemByCall(callId);
		
		if (product == null) {
			return;
		}
		
		int oldCredits = player.getDetails().getCredits();
		
		if (oldCredits >= product.getCredits()) {
			
			Item item = Roseau.getDataAccess().getInventory().newItem(product.getDefinition().getId(), player.getDetails().getId(), "");
			
			player.send(new SYSTEMBROADCAST("Buying successful!"));
			
			// Update the player connected to the private room
			//   (because PURCHASE is handled by the connection on the main game server, but we need to update the connection
			//    inside the hotel room)
			//
			
			Player privateRoomPlayer = player.getPrivateRoomPlayer();
			privateRoomPlayer.getInventory().getItems().add(item);
			
			player.send(new ADDSTRIPITEM());
			
		} else {
			player.send(new SYSTEMBROADCAST("You don't have enough credits to purchase this item!"));
		}
	}

}
