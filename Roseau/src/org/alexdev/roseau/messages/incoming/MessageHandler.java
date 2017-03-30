package org.alexdev.roseau.messages.incoming;

import java.util.HashMap;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.headers.Incoming;
import org.alexdev.roseau.messages.incoming.handshake.CHK_VERSION;
import org.alexdev.roseau.messages.incoming.handshake.GETAVAILABLESETS;
import org.alexdev.roseau.messages.incoming.handshake.TRY_LOGIN;
import org.alexdev.roseau.server.messages.ClientMessage;

import com.google.common.collect.Maps;

public class MessageHandler {
	
	private HashMap<Integer, MessageEvent> messages;

	public MessageHandler() {
		this.messages = Maps.newHashMap();
		this.register();
	}
	
	public void register() {
		this.messages.clear();
		this.registerHandshakePackets();

	}

	private void registerHandshakePackets() {
		this.messages.put(Incoming.CHK_VERSION, new CHK_VERSION());
		this.messages.put(Incoming.GETAVAILABLESETS, new GETAVAILABLESETS());
		this.messages.put(Incoming.TRY_LOGIN, new TRY_LOGIN());
	}
	
	public void handleRequest(Player player, ClientMessage message) {
		if (messages.containsKey(message.getMessageId())) {
			messages.get(message.getMessageId()).handle(player, message);
		}
	}

	public HashMap<Integer, MessageEvent> getMessages() {
		return messages;
	}

}
