package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.log.DateTime;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class MESSENGER_MSG implements OutgoingMessageComposer {

	private long timestamp;
	private String message;
	private String figure;
	private int fromID;
	
	public MESSENGER_MSG(int fromID, long timestamp, String message, String figure) {
		this.fromID = fromID;
		this.timestamp = timestamp;
		this.message = message;
		this.figure = figure;
	}

	@Override
	public void write(Response response) {
		response.init("MESSENGER_MSG");
		response.appendNewArgument(String.valueOf("-1"));
		response.appendNewArgument(String.valueOf(this.fromID));
        response.appendNewArgument("[]");
        response.appendNewArgument(DateTime.formatDateTime(this.timestamp));
        response.appendNewArgument(this.message);
        response.appendNewArgument(this.figure);
        response.appendNewArgument("");
	}
}
