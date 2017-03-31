package org.alexdev.roseau.messages.outgoing.room;

import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class USERS implements OutgoingMessageComposer {

	private Room room;

	public USERS(Room room) {
		this.room = room;
	}

	@Override
	public void write(Response response) {
		response.init("USERS" + (char)13 + " " + "Alex" + " " +
	"sd=001/0&hr=008/231,201,163&hd=002/255,204,153&ey=001/0&fc=001/255,204,153&bd=001/255,204,153&lh=001/255,204,153&rh=001/255,204,153&ch=008/255,237,179&ls=002/255,237,179&rs=002/255,237,179&lg=006/149,120,78&sh=003/121,94,83" + 
				" " + room.getData().getModel().getDoorX() + " " + room.getData().getModel().getDoorY() + " " + room.getData().getModel().getDoorZ() + " " + "a random motto ok");
	}

}
