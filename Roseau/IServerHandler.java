package org.alexdev.roseau.server;

import org.alexdev.roseau.messages.MessageHandler;
import org.alexdev.roseau.server.netty.connections.SessionManager;

public abstract class IServerHandler {
	
	private int port;
	private String ip;
	private String extraData;
	
	private MessageHandler messages;
	private SessionManager sessionManager;
	
	public IServerHandler(String extraData) {
		this.messages = new MessageHandler();
		this.sessionManager = new SessionManager();
		this.extraData = extraData;
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

	public String getExtraData() {
		return extraData;
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}
}
