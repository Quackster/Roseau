package org.alexdev.roseau.messages.outgoing.handshake;

import org.alexdev.roseau.messages.headers.Outgoing;
import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class SECRETKEY implements OutgoingMessageComposer {

	private String key;
	
	public SECRETKEY(String key) {
		super();
		this.key = key;
	}

	@Override
	public void write(Response response) {
		response.init(Outgoing.SECRETKEY);
		response.appendString(this.key);
		//response.appendString("testing123");
	}
}
