package org.alexdev.roseau.game;

import java.util.concurrent.TimeUnit;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;

public class GameScheduler implements Runnable {

	private long tickRate = 0;
	
	@Override
	public void run() {
	
		if (!(Roseau.getGame().getPlayerManager().getPlayers().size() > 0)) {
			return;
		}
		
		// every 5 seconds
		if ((this.tickRate % TimeUnit.MINUTES.toSeconds(Roseau.getUtilities().getConfiguration().get("Scheduler", "credits.every.x.mins", Integer.class))) == 0) {
			
			for (Player player : Roseau.getGame().getPlayerManager().getMainServerPlayers()) {
				
				player.getDetails().setCredits(player.getDetails().getCredits() + Roseau.getUtilities().getConfiguration().get("Scheduler", "credits.every.x.amount", Integer.class));
				player.getDetails().sendCredits();
				player.getDetails().save();
			}
		}
		
		this.tickRate++;
	}

}
