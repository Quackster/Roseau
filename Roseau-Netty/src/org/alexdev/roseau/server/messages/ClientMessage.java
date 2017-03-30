package org.alexdev.roseau.server.messages;

public interface ClientMessage {

	public Integer readInt();
	public boolean readIntAsBool();
	public boolean readBoolean();
	public String readString();
	public byte[] readBytes(int len);
	public String getMessageBody();
	public int getMessageId();
}
