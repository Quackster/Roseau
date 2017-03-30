package org.alexdev.roseau.server;

import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;

public abstract class IPlayerNetwork {

	private int connectionId;
	private int serverPort;
	
	public IPlayerNetwork(int connectionId, int serverPort) {
		this.connectionId = connectionId;
		this.serverPort = serverPort;
	}
	
	public abstract void send(OutgoingMessageComposer response);
	public abstract void close();
	
	public int getConnectionId() {
		return connectionId;
	}

	public int getServerPort() {
		return serverPort;
	}

}
