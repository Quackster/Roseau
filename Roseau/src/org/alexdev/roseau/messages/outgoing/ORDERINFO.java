package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class ORDERINFO implements OutgoingMessageComposer {

	private String name;
	private int credits;

	public ORDERINFO(String name, int credits) {
		this.name = name;
		this.credits = credits;
	}

	@Override
	public void write(Response response) {
		response.init("ORDERINFO");
		response.appendNewArgument(name);
		response.appendNewArgument(String.valueOf(this.credits));
	}

}
