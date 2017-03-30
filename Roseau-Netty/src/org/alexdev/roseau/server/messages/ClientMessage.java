package org.alexdev.roseau.server.messages;

public interface ClientMessage {

	public String getHeader();
	public String getMessageBody();
	public String getArgument(int index);
	public String getArgument(int index, String delimeter);
}
