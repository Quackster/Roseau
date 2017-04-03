package org.alexdev.roseau.messages;

import org.alexdev.roseau.server.messages.Response;

public interface OutgoingMessageComposer {
	public void write(Response response);
}
