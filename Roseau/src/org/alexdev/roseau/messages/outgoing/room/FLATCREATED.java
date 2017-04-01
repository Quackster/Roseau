
package org.alexdev.roseau.messages.outgoing.room;

import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class FLATCREATED implements OutgoingMessageComposer {

	@Override
	public void write(Response response) {
		response.init("FLATCREATED");
		response.appendArgument(Integer.toString(1337));
		response.appendArgument("model_a");

	}

}
