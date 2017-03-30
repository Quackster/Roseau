package org.alexdev.roseau.server;

import org.alexdev.roseau.messages.incoming.MessageHandler;

public abstract class IServerHandler {
	
	private int port;
	private String ip;
	
	private MessageHandler messages;
	
	public IServerHandler() {
		this.messages = new MessageHandler();
	}
	
	public abstract boolean listenSocket();

	public int getPort() {
		return port;
	}

	public String getIp() {
		return ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public MessageHandler getMessageHandler() {
		return messages;
	}
	
}
