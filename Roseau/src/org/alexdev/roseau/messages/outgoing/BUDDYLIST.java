package org.alexdev.roseau.messages.outgoing;

import java.util.List;

import org.alexdev.roseau.game.messenger.MessengerUser;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.log.DateTime;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class BUDDYLIST implements OutgoingMessageComposer {

	private List<MessengerUser> friends;
	private int offlineID; 
	
	public BUDDYLIST(List<MessengerUser> friends, int offlineID) {
		this.friends = friends;
		this.offlineID = offlineID;
	}

	@Override
	public void write(Response response) {

		response.init("BUDDYLIST");

		for (MessengerUser user : this.friends) {
			
			boolean forceOffline = false;
			
			if (this.offlineID > 0) {
				if (user.getDetails().getID() == this.offlineID) {
					forceOffline = true;
				}
			}
			
			user.update();
			
			response.appendNewArgument(String.valueOf(user.getDetails().getID()));
			response.appendTabArgument(user.getDetails().getName()); // friend name

			boolean hotelView = true;
			Room room = null;
			
			String location = "";

			Player player = user.getPlayer();

			if (player != null && !forceOffline) {

				if (player.getPrivateRoomPlayer() != null) {

					room = player.getPrivateRoomPlayer().getRoomUser().getRoom();

					if (room != null) {
						hotelView = false;
					}
				} 

				if (player.getPublicRoomPlayer() != null) {

					room = player.getPublicRoomPlayer().getRoomUser().getRoom();

					if (room != null) {
						hotelView = false;
					}
				}

				if (!hotelView) {

					if (room.getData().getRoomType() == RoomType.PRIVATE) {
						location = "In a user flat";
					} 

					if (room.getData().getRoomType() == RoomType.PUBLIC) {
						location = room.getData().getName();
					}

				} else {
					location = "On Hotel View";
				}
			}

			response.appendTabArgument(location);
			response.appendNewArgument(user.getDetails().getMission()); // messenger motto
			response.appendTabArgument(DateTime.formatDateTime(user.getDetails().getLastOnline())); // last online
		}
	}

}
