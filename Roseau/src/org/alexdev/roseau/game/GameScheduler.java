package org.alexdev.roseau.game;

import java.util.List;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.log.Log;

public class GameScheduler implements Runnable {

	private long tickRate = 0;
	
	@Override
	public void run() {
		
		try {
			Log.println("credits: " + GameVariables.CREDITS_EVERY_SEC);
			
			List<Player> players = Roseau.getGame().getPlayerManager().getMainServerPlayers();
			
			// every 5 seconds
			if ((this.tickRate % GameVariables.CREDITS_EVERY_SEC) == 0) {
				
				for (int i = 0; i < players.size(); i++) {
					
					Player player = players.get(i);
					
					player.getDetails().setCredits(player.getDetails().getCredits() + GameVariables.CREDITS_EVERY_AMOUNT);
					player.getDetails().sendCredits();
					player.getDetails().save();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.tickRate++;
	}

}
