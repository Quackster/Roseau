package org.alexdev.roseau.messages.outgoing.user;

import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class WALLETBALANCE implements OutgoingMessageComposer {

	private int credits;
	
	public WALLETBALANCE(int credits) {
		this.credits = credits;
	}

	@Override
	public void write(Response response) {
		response.init("WALLETBALANCE");
		response.appendNewArgument(String.valueOf(this.credits));
	}

}
