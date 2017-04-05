package org.alexdev.roseau.messages;

import java.util.HashMap;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.incoming.APPROVENAME;
import org.alexdev.roseau.messages.incoming.CREATEFLAT;
import org.alexdev.roseau.messages.incoming.DANCE;
import org.alexdev.roseau.messages.incoming.FINDUSER;
import org.alexdev.roseau.messages.incoming.GETCREDITS;
import org.alexdev.roseau.messages.incoming.GETUNITUSERS;
import org.alexdev.roseau.messages.incoming.GOAWAY;
import org.alexdev.roseau.messages.incoming.GOTOFLAT;
import org.alexdev.roseau.messages.incoming.INFORETRIEVE;
import org.alexdev.roseau.messages.incoming.INITUNITLISTENER;
import org.alexdev.roseau.messages.incoming.LOGIN;
import org.alexdev.roseau.messages.incoming.MESSENGER_INIT;
import org.alexdev.roseau.messages.incoming.MESSENGER_SENDMSG;
import org.alexdev.roseau.messages.incoming.MOVE;
import org.alexdev.roseau.messages.incoming.REGISTER;
import org.alexdev.roseau.messages.incoming.SEARCHBUSYFLATS;
import org.alexdev.roseau.messages.incoming.SEARCHFLATFORUSER;
import org.alexdev.roseau.messages.incoming.STATUSOK;
import org.alexdev.roseau.messages.incoming.STOP;
import org.alexdev.roseau.messages.incoming.TALK;
import org.alexdev.roseau.messages.incoming.TRYFLAT;
import org.alexdev.roseau.messages.incoming.UPDATE;
import org.alexdev.roseau.messages.incoming.VERSIONCHECK;
import org.alexdev.roseau.messages.outgoing.CLOSE_UIMAKOPPI;
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
		this.registerUserPackets();
		this.registerHandshakePackets();
		this.registerLoginPackets();
		this.registerRegisterPackets();
		this.registerNavigatorPackets();
		this.registerRoomPackets();
		this.registerMessengerPackets();
		this.registerItemPackets();

	}

	private void registerUserPackets() {
		this.messages.put("UPDATE", new UPDATE());
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
		this.messages.put("SEARCHFLATFORUSER", new SEARCHFLATFORUSER());
		this.messages.put("SEARCHBUSYFLATS", new SEARCHBUSYFLATS());
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
		this.messages.put("TRYFLAT", new TRYFLAT());
		this.messages.put("GOTOFLAT", new GOTOFLAT());
		this.messages.put("CLOSE_UIMAKOPPI", new CLOSE_UIMAKOPPI());
		
	}
	
	private void registerMessengerPackets() {
		this.messages.put("MESSENGERINIT", new MESSENGER_INIT());
		this.messages.put("MESSENGER_SENDMSG", new MESSENGER_SENDMSG());
	}

	private void registerItemPackets() {
		// TODO Auto-generated method stub
		
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
