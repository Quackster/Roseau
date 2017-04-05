package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class MYPERSISTENTMSG implements OutgoingMessageComposer {

	private String personalMessage;
	
	public MYPERSISTENTMSG(String personalMessage) {
		this.personalMessage = personalMessage;
	}

	@Override
	public void write(Response response) {
		response.init("MYPERSISTENTMSG");
		response.appendNewArgument(this.personalMessage);
	}

}
