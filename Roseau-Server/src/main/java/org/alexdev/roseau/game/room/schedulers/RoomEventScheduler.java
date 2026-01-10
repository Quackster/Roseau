package org.alexdev.roseau.game.room.schedulers;

import org.alexdev.roseau.game.room.Room;
import org.oldskooler.simplelogger4j.SimpleLog;

public class RoomEventScheduler implements Runnable {
    private static final SimpleLog logger = SimpleLog.of(RoomEventScheduler.class);

	private Room room;
	
	public RoomEventScheduler(Room room) {
		this.room = room;
	}

	@Override
	public void run() {
		
		for (RoomEvent event : this.room.getEvents()) {
			try {
				event.tick();
			} catch (Exception e) {
				logger.error("Error in room event tick", e);
			}
		}
		
		/*for (int i = 0; i < this.room.getEvents().size(); i++) {
			
			RoomEvent event = this.room.getEvents().get(i);
			event.tick();
		}*/
	}

}
