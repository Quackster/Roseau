package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class STUFFDATAUPDATE implements OutgoingMessageComposer {

	private Item item;
	private String customData;
	
	public STUFFDATAUPDATE(Item item, String customData) {
		this.item = item;
		this.customData = customData;
	}

	@Override
	public void write(Response response) {
		response.init("STUFFDATAUPDATE");
		response.appendNewArgument(this.item.getPacketID());
		response.appendPartArgument("");
		response.appendPartArgument(this.item.getDefinition().getDataClass());
		response.appendPartArgument(this.customData);
	}

}
