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

			/*		msg.appendArgument(Integer.toString(this.ID), '|');
		msg.appendArgument("blunk", '|');
		if(this.definition.behaviour.STUFF)
		{
			msg.appendArgument("S", '|');
		}
		else if(this.definition.behaviour.ITEM)
		{
			msg.appendArgument("I", '|');
		}
		msg.appendArgument(Integer.toString(stripSlotID), '|');
		msg.appendArgument(this.definition.sprite, '|');
		msg.appendArgument(this.definition.name, '|');
		if(this.definition.behaviour.STUFF)
		{
			msg.appendArgument(this.customData, '|');
			msg.appendArgument(Integer.toString(this.definition.length), '|');
			msg.appendArgument(Integer.toString(this.definition.width), '|');
			msg.appendArgument(this.definition.color, '|');
		}
		else if(this.definition.behaviour.ITEM)
		{
			msg.appendArgument(this.customData, '|');
			msg.appendArgument(this.customData, '|');
		}*/

			response.appendNewArgument("roseau");
			response.appendArgument(String.valueOf(item.getId()), ';');
			response.appendArgument("0", ';');

			if (item.getDefinition().getBehaviour().isSTUFF()) {
				response.appendArgument("S", ';');
			} else if (item.getDefinition().getBehaviour().isITEM()) {
				response.appendArgument("I", ';');
			}
			
			response.appendArgument(String.valueOf(item.getId()), ';');
			response.appendArgument(item.getDefinition().getSprite(), ';');
			response.appendArgument(item.getDefinition().getName(), ';');
			
			if (item.getDefinition().getBehaviour().isSTUFF()) {
				
				response.appendArgument(item.getCustomData(), ';');
				response.appendArgument(String.valueOf(item.getDefinition().getLength()), ';');
				response.appendArgument(String.valueOf(item.getDefinition().getWidth()), ';');
				response.appendArgument(item.getDefinition().getColor(), ';');
				
			} else if (item.getDefinition().getBehaviour().isITEM()) {
				response.appendArgument(item.getCustomData(), ';');
				response.appendArgument(item.getCustomData(), ';');
			}
			
			response.appendArgument("", '/');
		}
	}

}
