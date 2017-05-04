package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class OPEN_UIMAKOPPI extends OutgoingMessageComposer {

	@Override
	public void write() {
		response.init("OPEN_UIMAKOPPI");
	}

}
