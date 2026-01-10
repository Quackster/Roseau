package org.alexdev.roseau.game.messenger;

public class MessengerMessage {
	private int id;
	private int toId;
	private int fromId;
	private long timeSent;
	private String message;
	
	public MessengerMessage(int id, int toId, int fromId, long timeSent, String message) {
		this.id = id;
		this.toId = toId;
		this.fromId = fromId;
		this.timeSent = timeSent;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public int getToId() {
		return toId;
	}

	public int getFromId() {
		return fromId;
	}

	public long getTimeSent() {
		return timeSent;
	}

	public String getMessage() {
		return message;
	}
	
}
