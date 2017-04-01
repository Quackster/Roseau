package org.alexdev.roseau.messages.incoming;

import java.util.HashMap;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.incoming.handshake.VERSIONCHECK;
import org.alexdev.roseau.messages.incoming.login.GETCREDITS;
import org.alexdev.roseau.messages.incoming.login.INFORETRIEVE;
import org.alexdev.roseau.messages.incoming.login.LOGIN;
import org.alexdev.roseau.messages.incoming.navigator.GETUNITUSERS;
import org.alexdev.roseau.messages.incoming.navigator.INITUNITLISTENER;
import org.alexdev.roseau.messages.incoming.register.APPROVENAME;
import org.alexdev.roseau.messages.incoming.register.FINDUSER;
import org.alexdev.roseau.messages.incoming.register.REGISTER;
import org.alexdev.roseau.messages.incoming.room.CREATEFLAT;
import org.alexdev.roseau.messages.incoming.room.GOAWAY;
import org.alexdev.roseau.messages.incoming.room.STATUSOK;
import org.alexdev.roseau.messages.incoming.room.user.DANCE;
import org.alexdev.roseau.messages.incoming.room.user.MOVE;
import org.alexdev.roseau.messages.incoming.room.user.STOP;
import org.alexdev.roseau.messages.incoming.room.user.TALK;
import org.alexdev.roseau.server.messages.ClientMessage;

import com.google.common.collect.Maps;

public class MessageHandler {
	
	private HashMap<String, MessageEvent> messages;

	public MessageHandler() {
		this.messages = Maps.newHashMap();
		this.register();
	}
	
	public void register() {
		this.messages.clear();
		this.registerHandshakePackets();
		this.registerLoginPackets();
		this.registerRegisterPackets();
		this.registerNavigatorPackets();
		this.registerRoomPackets();
	}

	private void registerHandshakePackets() {
		this.messages.put("VERSIONCHECK", new VERSIONCHECK());
	}
	
	private void registerLoginPackets() {
		this.messages.put("LOGIN", new LOGIN());
		this.messages.put("INFORETRIEVE", new INFORETRIEVE());
		this.messages.put("GETCREDITS", new GETCREDITS());
	}
		
	private void registerRegisterPackets() {
		this.messages.put("APPROVENAME", new APPROVENAME());
		this.messages.put("FINDUSER", new FINDUSER());
		this.messages.put("REGISTER", new REGISTER());
	}

	private void registerNavigatorPackets() {
		this.messages.put("INITUNITLISTENER", new INITUNITLISTENER());
		this.messages.put("GETUNITUSERS", new GETUNITUSERS());
	}
	
	private void registerRoomPackets() {
		this.messages.put("STATUSOK", new STATUSOK());
		this.messages.put("Move", new MOVE());
		this.messages.put("Dance", new DANCE());
		this.messages.put("STOP", new STOP());
		this.messages.put("CHAT", new TALK());
		this.messages.put("SHOUT", new TALK());
		this.messages.put("WHISPER", new TALK());
		this.messages.put("GOAWAY", new GOAWAY());
		this.messages.put("CREATEFLAT", new CREATEFLAT());
	}
	
	
	public void handleRequest(Player player, ClientMessage message) {
		if (messages.containsKey(message.getHeader())) {
			messages.get(message.getHeader()).handle(player, message);
		}
	}

	public HashMap<String, MessageEvent> getMessages() {
		return messages;
	}

}
