package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.messages.error.ErrorType;
import org.alexdev.roseau.server.messages.Response;

public class ERROR implements OutgoingMessageComposer {

	private String errorMessage;
	private ErrorType errorType;

	public ERROR(ErrorType errorType, String errorMessage) {
		this.errorType = errorType;
		this.errorMessage = errorMessage;
	}

	public ERROR(String errorMessage) {
		this.errorType = ErrorType.GENERIC;
		this.errorMessage = errorMessage;
	}

	@Override
	public void write(Response response) {
		response.init("ERROR");

		if (this.errorType == ErrorType.MODERATOR) {
			response.appendArgument("MODERATOR WARNING");
			response.appendArgument(this.errorMessage, '/');
		} else {
			response.appendArgument(this.errorMessage);
		}
	}

}
