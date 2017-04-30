package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class JUMPDATA implements OutgoingMessageComposer {

	private String name;
	private String data;

	public JUMPDATA(String name, String data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public void write(Response response) {
		response.init("JUMPDATA");
		response.appendNewArgument(this.name);
		response.appendNewArgument(this.data);
		
	}

}
