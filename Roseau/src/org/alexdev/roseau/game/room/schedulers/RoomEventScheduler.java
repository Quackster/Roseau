package org.alexdev.roseau.game.room.schedulers;

import org.alexdev.roseau.game.room.Room;

public class RoomEventScheduler implements Runnable {

	private Room room;
	
	public RoomEventScheduler(Room room) {
		this.room = room;
	}

	@Override
	public void run() {
		
		for (RoomEvent event : this.room.getEvents()) {
			event.tick();
		}
		
		/*for (int i = 0; i < this.room.getEvents().size(); i++) {
			
			RoomEvent event = this.room.getEvents().get(i);
			event.tick();
		}*/
	}

}
