package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class SHOWPROGRAM implements OutgoingMessageComposer {

	private String[] parameters;

	public SHOWPROGRAM(String[] parameters) {
		this.parameters = parameters;
	}

	@Override
	public void write(Response response) {
		response.init("SHOWPROGRAM");
		if (parameters.length > 1) {
			for (int i = 0; i < parameters.length; i++) {	
				String parameter = parameters[i];
				response.appendArgument(parameter);
			}
		}
	}

}
