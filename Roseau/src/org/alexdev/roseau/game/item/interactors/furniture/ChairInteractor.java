package org.alexdev.roseau.game.item.interactors.furniture;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.interactors.Interaction;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.outgoing.OPEN_GAMEBOARD;

public class ChairInteractor extends Interaction {

	public ChairInteractor(Item item) {
		super(item);
	}

	@Override
	public void onTrigger(Player player) {	}

	@Override
	public void onStoppedWalking(Player player) {
		player.getRoomUser().getPosition().setRotation(item.getPosition().getRotation());
		player.getRoomUser().removeStatus("dance");
		player.getRoomUser().removeStatus("lay");
		player.getRoomUser().setStatus("sit", " " + String.valueOf(player.getRoomUser().getPosition().getZ() + definition.getHeight()), true, -1);
		
		if (player.getRoomUser().getRoom().getData().getModelName().equals("hallA")) {
			
			player.send(new OPEN_GAMEBOARD("TicTacToe", "0", item));
		}
	}	
}
