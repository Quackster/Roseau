package org.alexdev.roseau.messages.outgoing.handshake;

import org.alexdev.roseau.messages.headers.Outgoing;
import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class RIGHTS implements OutgoingMessageComposer {

	@Override
	public void write(Response response) {
		response.init(Outgoing.RIGHTS);
		response.appendString("fuse_use_club_outfitsfuse_use_club_badgefuse_use_special_room_layoutsfuse_room_queue_clubfuse_room_queue_defaultfuse_use_club_dancefuse_priority_accessfuse_habbo_chooserfuse_furni_chooserdefault");
	}

}
