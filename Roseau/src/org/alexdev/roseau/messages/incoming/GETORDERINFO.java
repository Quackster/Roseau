package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.catalogue.CatalogueItem;
import org.alexdev.roseau.game.item.ItemDefinition;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class GETORDERINFO implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		String callId = reader.getArgument(1);
		
		CatalogueItem item = Roseau.getGame().getCatalogueManager().getItemByCall(callId);
		ItemDefinition definition = item.getDefinition();
		
		if (definition == null) {
			return;
		}
		
		Log.println(item.getDefinition().getSprite());

	}

}
