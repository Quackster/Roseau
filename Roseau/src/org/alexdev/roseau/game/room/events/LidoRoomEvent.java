package org.alexdev.roseau.game.room.events;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.outgoing.SHOWPROGRAM;

public class LidoRoomEvent extends RoomEvent {

	public LidoRoomEvent(Room room) {
		super(room);
	}

	@Override
	public void tick() {
		
		//sendAllP "AGBIGSPLASH position " & AGLoc & Chr(1) & 
		//"AGcam1 transition cameraPan@r
		//"AGcam1 targetcamera " & frmMain.LidoCammed.Text & "", 36

		
		String x = String.valueOf(Roseau.getUtilities().getRandom().nextInt(14) + 1);
		String y = String.valueOf(Roseau.getUtilities().getRandom().nextInt(2) + 1);
		
		boolean RND = Roseau.getUtilities().getRandom().nextBoolean();
		
		/*room.send(new SHOWPROGRAM(new String[] {"BIGSPLASH", "position", x, y}));
		room.send(new SHOWPROGRAM(new String[] {"cam1", "transition","cameraPan" }));*/
		
		/*if (RND) {
		room.send(new SHOWPROGRAM(new String[] {"cam1", "targetcamera","1" }));
		} else {
			
		
			room.send(new SHOWPROGRAM(new String[] {"cam1", "setcamera", y }));
		}*/
	}

}
