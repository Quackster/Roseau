package org.alexdev.roseau.messages.outgoing.items;

import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class SHOW_PROGRAM implements OutgoingMessageComposer {

	private String program;
	private String data;

	public SHOW_PROGRAM(String program, String data) {
		this.program = program;
		this.data = data;
	}

	@Override
	public void write(Response response) {
		response.init("SHOW_PROGRAM");
		response.appendArgument(this.program);
		response.appendArgument(this.data);
	}

}
