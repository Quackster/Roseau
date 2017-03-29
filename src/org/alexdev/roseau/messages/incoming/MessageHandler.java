package org.alexdev.roseau.messages.incoming;

import java.util.HashMap;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.server.messages.ClientMessage;

import com.google.common.collect.Maps;

public class MessageHandler {
	
	private HashMap<Short, MessageEvent> messages;

	public MessageHandler() {
		this.messages = Maps.newHashMap();
		this.register();
	}
	
	public void register() {
		
		this.messages.clear();
		
		this.registerHandshakePackets();

	}

	private void registerHandshakePackets() {

	}
	
	public void handleRequest(Player player, ClientMessage message) {
		if (messages.containsKey(message.getMessageId())) {
			messages.get(message.getMessageId()).handle(player, message);
		}
	}

	public HashMap<Short, MessageEvent> getMessages() {
		return messages;
	}

}
