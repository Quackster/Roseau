package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class NAME_UNACCEPTABLE extends OutgoingMessageComposer {

	@Override
	public void write() {
		response.init("NAME_UNACCEPTABLE");
	}

}
