package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class FLAT_PROPERTY implements OutgoingMessageComposer {

	private String property;
	private String data;
	
	public FLAT_PROPERTY(String property, String data) {
		this.property = property;
		this.data = data;
	}

	@Override
	public void write(Response response) {
		response.init("FLAT_PROPERTY");
		response.appendNewArgument(this.property);
		response.appendPartArgument(this.data);

	}

}
