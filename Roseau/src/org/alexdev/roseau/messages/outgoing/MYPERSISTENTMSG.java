package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class MYPERSISTENTMSG extends OutgoingMessageComposer {

	private String personalMessage;
	
	public MYPERSISTENTMSG(String personalMessage) {
		this.personalMessage = personalMessage;
	}

	@Override
	public void write() {
		response.init("MYPERSISTENTMSG");
		response.appendNewArgument(this.personalMessage);
	}

}
