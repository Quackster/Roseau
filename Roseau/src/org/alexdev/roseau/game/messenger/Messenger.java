package org.alexdev.roseau.game.messenger;

import java.util.List;
import java.util.Optional;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.outgoing.BUDDYADDREQUESTS;
import org.alexdev.roseau.messages.outgoing.BUDDYLIST;

public class Messenger {

	private boolean initalised;
	private Player player;

	private List<MessengerUser> friends;
	private List<MessengerUser> requests;

	public Messenger(Player player) {
		this.player = player;
		this.initalised = false;
	}

	public void load() {
		this.friends = Roseau.getDao().getMessenger().getFriends(player.getDetails().getID());
		this.requests = Roseau.getDao().getMessenger().getRequests(player.getDetails().getID());
	}

	public boolean hasRequest(int id) {
		return this.getRequest(id) != null;
	}
	
	public boolean isFriend(int id) {
		return this.getFriend(id) != null;
	}
	
	public MessengerUser getFriend(int id) {
		
		Optional<MessengerUser> friend = this.friends.stream().filter(f -> f.getDetails().getID() == id).findFirst();

		if (friend.isPresent()) {
			return friend.get();
		} else {
			return null;
		}
	}
	
	public MessengerUser getRequest(int id) {

		Optional<MessengerUser> request = this.requests.stream().filter(f -> f.getDetails().getID() == id).findFirst();

		if (request.isPresent()) {
			return request.get();
		} else {
			return null;
		}
	}

	public void removeFriend(int id) {
		MessengerUser user = this.getFriend(id);
		this.friends.remove(user);
	}
	

	public void sendRequests() {
		
		if (!(this.requests.size() > 0)) {
			return;
		}
		
		this.player.send(new BUDDYADDREQUESTS(this.requests));
	}
	

	public void sendFriends() {
		this.sendFriends(-1);
	}
	
	public void sendFriends(int offlineID) {
		
		if (!(this.friends.size() > 0)) {
			//return;
		}
		
		Log.println("(" + this.player.getDetails().getName() + "): sending friends");
		this.player.send(new BUDDYLIST(this.friends, offlineID));
	}
	
	public void sendStatus() {

		//AbstractResponse message = new MessengerUpdateMessageComposer(new MessengerUser(this.player.getDetails().getId()), forceOffline);

		for (MessengerUser friend : this.friends) {

			if (friend.isOnline()) {
				if (friend.getPlayer().getMessenger().hasInitalised()) {
					friend.getPlayer().getMessenger().sendFriends();
				}
			}
		}
	}
	
	public void sendStatus(int offlineUserID) {

		//AbstractResponse message = new MessengerUpdateMessageComposer(new MessengerUser(this.player.getDetails().getId()), forceOffline);

		for (MessengerUser friend : this.friends) {

			if (friend.isOnline()) {
				if (friend.getPlayer().getMessenger().hasInitalised()) {
					friend.getPlayer().getMessenger().sendFriends(offlineUserID);
				}
			}
		}
	}
	
	public void dispose() {

		this.sendStatus(this.player.getDetails().getID());
		
		if (this.friends != null) {
			this.friends.clear();
			this.friends = null;
		}

		if (this.requests != null) {
			this.requests.clear();
			this.requests = null;
		}

		this.player = null;
	}

	public List<MessengerUser> getFriends() {
		return friends;
	}

	public List<MessengerUser> getRequests() {
		return requests;
	}

	public boolean hasInitalised() {
		return initalised;
	}

	public void setInitalised(boolean initalised) {
		this.initalised = initalised;
	}
}
