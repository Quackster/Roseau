package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class REMOVEWALLITEM implements OutgoingMessageComposer {

	private String id;

	public REMOVEWALLITEM(String id) {
		this.id = id;
	}

	@Override
	public void write(Response response) {
		response.init("REMOVEITEM");
		response.appendNewArgument(this.id);
	}

}
