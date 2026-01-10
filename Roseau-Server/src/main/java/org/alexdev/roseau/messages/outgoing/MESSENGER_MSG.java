package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.messenger.MessengerMessage;
import org.alexdev.roseau.log.DateTime;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class MESSENGER_MSG extends OutgoingMessageComposer {
	private long timestamp;
	private String message;
	private String figure;
	private int fromId;
	private int messageId;

	public MESSENGER_MSG(int messageId, int fromId, long timestamp, String message, String figure) {
		this.messageId = messageId;
		this.fromId = fromId;
		this.timestamp = timestamp;
		this.message = message;
		this.figure = figure;
	}

	public MESSENGER_MSG(MessengerMessage messengerMessage, String figure) {
		this.messageId = messengerMessage.getId();
		this.fromId = messengerMessage.getFromId();
		this.timestamp = messengerMessage.getTimeSent();
		this.message = messengerMessage.getMessage();
		this.figure = figure;
	}

	@Override
	public void write() {
		response.init("MESSENGER_MSG");
		response.appendNewArgument(String.valueOf(this.messageId));
		response.appendNewArgument(String.valueOf(this.fromId));
        response.appendNewArgument("[]");
        response.appendNewArgument(DateTime.formatDateTime(this.timestamp));
        response.appendNewArgument(this.message);
        response.appendNewArgument(this.figure);
        response.appendNewArgument("");
	}
}
