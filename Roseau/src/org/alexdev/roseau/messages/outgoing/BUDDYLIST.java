package org.alexdev.roseau.messages.outgoing;

import java.util.List;

import org.alexdev.roseau.game.messenger.MessengerUser;
import org.alexdev.roseau.game.player.Player;
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

		/*		msg.appendNewArgument(Integer.toString(this.ID));
		msg.appendTabArgument(this.name);
		msg.appendTabArgument(this.messengerMotto);
		
		// Determine location
		String location = "";
		CommunicationHandler client = HabboHotel.getGameClients().getClientOfUser(this.ID);
		if(client != null)
		{
			if(client.getSpaceInstance() == null)
			{
				location = "On Hotel View";
			}
			else
			{
				if(client.getSpaceInstance().getInfo().isUserFlat())
				{
					location = "In a user flat";
				}
				else
				{
					location = client.getSpaceInstance().getInfo().name;
				}
			}
		}
		msg.appendNewArgument(location);
		if(client == null)
		{
			msg.appendTabArgument(TimeHelper.formatDateTime(this.lastActivity));
		}
		else
		{
			// Current date & time (user is online)
			msg.appendTabArgument(TimeHelper.formatDateTime());
		}*/
		
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
			response.appendTabArgument(user.getDetails().getPersonalGreeting()); 
			
			Player player = user.getPlayer();

			if (player != null && !forceOffline) {
				response.appendNewArgument(player.getMessenger().getLocation());
			} else {
				response.appendNewArgument("");
			}
			
			if (player != null && !forceOffline) {
				response.appendTabArgument(DateTime.formatDateTime(user.getDetails().getLastOnline())); // last online
			} else {
				
				response.appendTabArgument(DateTime.formatDateTime()); // last online	
			}
		}
	}

}
