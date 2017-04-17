package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class DOOR_OUT  implements OutgoingMessageComposer {

	private Item item;
	private String player;
	
	public DOOR_OUT(Item item, String player) {
		this.item = item;
		this.player = player;
	}

	@Override
	public void write(Response response) {
		response.init("DOOR_OUT");
		response.appendArgument(Integer.toString(item.getID()));
		response.appendPartArgument(this.player);
		response.appendPartArgument(this.item.getDefinition().getSprite());
	}

}
