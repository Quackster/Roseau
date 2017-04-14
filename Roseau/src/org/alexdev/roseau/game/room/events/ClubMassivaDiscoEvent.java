package org.alexdev.roseau.game.room.events;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.outgoing.SHOWPROGRAM;

public class ClubMassivaDiscoEvent extends RoomEvent {

	private int currentLampID;

	public ClubMassivaDiscoEvent(Room room) {
		super(room);
		this.currentLampID = -1;
	}

	@Override
	public void tick() {

		if (this.currentLampID == -1) {
			this.currentLampID = this.getNewLampID();
		}

		if (this.canTick(10)) { // 30 seconds 
			room.send(new SHOWPROGRAM(new String[] {"lamp", "setlamp", String.valueOf(this.currentLampID)}));
			this.currentLampID = this.getNewLampID();
		}

		if (this.canTick(10)) { // 5 seconds 
			String discoID = String.valueOf(Roseau.getUtilities().getRandom().nextInt(14) + 1);

			boolean otherPaletes = Roseau.getUtilities().getRandom().nextBoolean();

			room.send(new SHOWPROGRAM(new String[] {"df1", "setfloora", discoID }));

			if (otherPaletes) {
				room.send(new SHOWPROGRAM(new String[] {"df1", "setfloorb", discoID }));
			}

			room.send(new SHOWPROGRAM(new String[] {"df2", "setfloora", discoID }));

			if (otherPaletes) {
				room.send(new SHOWPROGRAM(new String[] {"df2", "setfloorb", discoID }));
			}

			room.send(new SHOWPROGRAM(new String[] {"df3", "setfloora", discoID }));

			if (otherPaletes) {
				room.send(new SHOWPROGRAM(new String[] {"df3", "setfloorb", discoID }));
			}
		}

		/*room.send(new SHOWPROGRAM(new String[] {"df1", "setfloora", String.valueOf(Roseau.getUtilities().getRandom().nextInt(14) + 1)}));
		room.send(new SHOWPROGRAM(new String[] {"df1", "setfloorb", String.valueOf(Roseau.getUtilities().getRandom().nextInt(14) + 1)}));

		room.send(new SHOWPROGRAM(new String[] {"df2", "setfloora", String.valueOf(Roseau.getUtilities().getRandom().nextInt(14) + 1)}));
		room.send(new SHOWPROGRAM(new String[] {"df2", "setfloorb", String.valueOf(Roseau.getUtilities().getRandom().nextInt(14) + 1)}));

		room.send(new SHOWPROGRAM(new String[] {"df3", "setfloora", String.valueOf(Roseau.getUtilities().getRandom().nextInt(14) + 1)}));
		room.send(new SHOWPROGRAM(new String[] {"df3", "setfloorb", String.valueOf(Roseau.getUtilities().getRandom().nextInt(14) + 1)}));*/

		this.increaseTicked();

	}

	private int getNewLampID() {
		
		int lampID = Roseau.getUtilities().getRandom().nextInt(5) + 1;
		
		if (lampID == this.currentLampID) {
			return this.getNewLampID();
		}
		
		return lampID;
	}
	

}
