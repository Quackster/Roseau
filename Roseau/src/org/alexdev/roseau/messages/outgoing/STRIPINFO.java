package org.alexdev.roseau.messages.outgoing;

import java.util.List;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class STRIPINFO implements OutgoingMessageComposer {

	private List<Item> items;
	
	public STRIPINFO(List<Item> items) {
		this.items = items;
	}

	@Override
	public void write(Response response) {
		response.init("STRIPINFO");
		
		for (Item item : this.items) {
			response.appendNewArgument("BLSI");
			response.appendArgument(String.valueOf(item.getId()), ';');
			response.appendArgument("0", ';');
			response.appendArgument("S", ';');
			response.appendArgument(String.valueOf(item.getId()), ';');
			response.appendArgument(item.getDefinition().getSprite(), ';');
			response.appendArgument(item.getDefinition().getName(), ';');
			response.appendArgument(item.getDefinition().getDescription(), ';');
			response.appendArgument("1;1", ';');
			response.appendArgument(item.getDefinition().getColor(), ';');
			response.appendArgument("", '/');
		}
	}

}
