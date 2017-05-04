package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class YOUARENOTCONTROLLER extends OutgoingMessageComposer {

	@Override
	public void write() {
		response.init("YOUARENOTCONTROLLER");
	}

}
