package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.FLATCREATED;
import org.alexdev.roseau.server.messages.ClientMessage;

public class CREATEFLAT implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		player.send(new FLATCREATED());
		
		// Kick off public room user
		Player publicRoomPlayer = Roseau.getGame().getPlayerManager().getIdenticalPlayer(player.getDetails().getId(), player.getNetwork().getConnectionId());
		
		if (publicRoomPlayer != null) {
			publicRoomPlayer.getNetwork().close();
		}
		
		/*[-2146793316] Received: CREATEFLAT / /first floor/ROOM NAME/model_a/closed/1
[04/03/2017 15:37:42] [ROSEAU] >> SENT: #FLATCREATED 1337 model_a##
[04/03/2017 15:37:42] [ROSEAU] >> [-1914766124] Disconnection from 127.0.0.1
[04/03/2017 15:37:42] [ROSEAU] >> [-2146793316] Received: SETFLATINFO / //description=ROOM DESCRIPTION
password=wef
allsuperuser=1*/
		
		

	}

}
