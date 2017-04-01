package org.alexdev.roseau.messages.incoming.room;

import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class DOORFLAT implements OutgoingMessageComposer {

	@Override
	public void write(Response response) {
		response.init("DOORFLAT");
		response.appendNewArgument("1");
		response.appendNewArgument("2");
	}

}
