package org.alexdev.roseau.messages.outgoing.room.pool;

import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class JUMPDATA implements OutgoingMessageComposer {

	@Override
	public void write(Response response) {
		response.init("PT_PR");
		response.appendKV2Argument("0", "Alex");
		response.appendKV2Argument("1", "Alex");
		
	}

}
