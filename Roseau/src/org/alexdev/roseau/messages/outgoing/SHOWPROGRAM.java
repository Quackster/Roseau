package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class SHOWPROGRAM implements OutgoingMessageComposer {

	private String itemData;
	private String data;

	public SHOWPROGRAM(String itemData, String data) {
		this.itemData = itemData;
		this.data = data;
	}

	@Override
	public void write(Response response) {
		response.init("SHOWPROGRAM");
		response.appendArgument(this.itemData);
		response.appendArgument(this.data);
	}

}
