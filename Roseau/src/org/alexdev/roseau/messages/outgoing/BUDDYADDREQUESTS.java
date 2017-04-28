
package org.alexdev.roseau.messages.outgoing;

import java.util.List;

import org.alexdev.roseau.game.messenger.MessengerUser;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class BUDDYADDREQUESTS implements OutgoingMessageComposer {

	private List<MessengerUser> requests;

	public BUDDYADDREQUESTS(List<MessengerUser> requests) {
		this.requests = requests;
	}

	@Override
	public void write(Response response) {
		response.init("BUDDYADDREQUESTS");
		response.appendNewArgument("");

		for (MessengerUser user : this.requests) {
			response.appendPartArgument(user.getDetails().getName());
		}

	}

}
