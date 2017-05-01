package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.moderation.CallForHelp;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class CRYFORHELP implements OutgoingMessageComposer {

	private CallForHelp cfh;

	public CRYFORHELP(CallForHelp cfh) {
		this.cfh = cfh;
	}

	@Override
	public void write(Response msg) {
		msg.init("CRYFORHELP");
		msg.appendObject(this.cfh);
	}
}
