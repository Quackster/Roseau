package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.server.messages.Response;

public interface OutgoingMessageComposer {
	public void write(Response response);
}
