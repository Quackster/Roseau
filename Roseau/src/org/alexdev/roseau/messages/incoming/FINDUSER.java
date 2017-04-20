package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.MEMBERINFO;
import org.alexdev.roseau.server.messages.ClientMessage;

public class FINDUSER implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		try {
		String[] args = reader.getMessageBody().split("\t", 2);
		String name = args[0];
	
		
			if (name.length() > 0) {
				if (Roseau.getDao().getPlayer().isNameTaken(name)) {
					player.send(new MEMBERINFO());
				}		
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

}
