package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class MESSENGERREADY implements OutgoingMessageComposer {

	@Override
	public void write(Response response) {
		response.init("MESSENGERREADY");
	}

}
