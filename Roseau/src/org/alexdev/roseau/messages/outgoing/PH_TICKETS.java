package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class PH_TICKETS implements OutgoingMessageComposer {

	private int tickets;

	public PH_TICKETS(int tickets) {
		this.tickets = tickets;
	}

	@Override
	public void write(Response response) {
		response.init("PH_TICKETS");
		response.appendArgument(Integer.toString(this.tickets));
	}

}
