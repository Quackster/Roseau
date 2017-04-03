package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class JUMPDATA implements OutgoingMessageComposer {

	@Override
	public void write(Response response) {
		response.init("PT_PR");
		response.appendKV2Argument("0", "Alex");
		response.appendKV2Argument("1", "Alex");
		
	}

}
