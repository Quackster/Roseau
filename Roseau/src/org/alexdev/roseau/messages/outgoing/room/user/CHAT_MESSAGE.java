package org.alexdev.roseau.messages.outgoing.room.user;

import org.alexdev.roseau.game.room.entity.RoomEntity;
import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class CHAT_MESSAGE implements OutgoingMessageComposer {

	private RoomEntity roomEntity;
	private String talkMessage;
	private String header;

	public CHAT_MESSAGE(RoomEntity roomEntity, String talkMessage, String header) {
		this.roomEntity = roomEntity;
		this.talkMessage = talkMessage;
		this.header = header;
		
	}

	@Override
	public void write(Response response) {

		response.init(this.header);
		response.appendNewArgument(this.roomEntity.getEntity().getDetails().getUsername());
		response.appendArgument(this.talkMessage);
	}

}
