package org.alexdev.roseau.game.messenger;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.messages.outgoing.BUDDYADDREQUESTS;
import org.alexdev.roseau.messages.outgoing.BUDDYLIST;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
		this.friends = Roseau.getDao().getMessenger().getFriends(player.getDetails().getId());
		this.requests = Roseau.getDao().getMessenger().getRequests(player.getDetails().getId());
	}

	public boolean hasRequest(int id) {
		return getRequest(id) != null;
	}

	public boolean isFriend(int id) {
		return getFriend(id) != null;
	}

	public MessengerUser getFriend(int id) {
		return friends.stream()
			.filter(friend -> friend.getDetails().getId() == id)
			.findFirst()
			.orElse(null);
	}

	public MessengerUser getRequest(int id) {
		return requests.stream()
			.filter(request -> request.getDetails().getId() == id)
			.findFirst()
			.orElse(null);
	}

	public void removeFriend(int id) {
		getFriend(id);
		friends.removeIf(friend -> friend.getDetails().getId() == id);
	}

	public void sendRequests() {
		if (requests.isEmpty()) {
			return;
		}
		this.player.send(new BUDDYADDREQUESTS(this.requests));
	}

	public void sendFriends() {
		this.sendFriends(-1);
	}

	public void sendFriends(int offlineID) {
		this.friends.sort(
			Comparator.<MessengerUser>comparingLong(friend -> friend.getDetails().getLastOnline())
				.thenComparing(friend -> !friend.isOnline())
		);

		this.player.send(new BUDDYLIST(this.friends, offlineID));
	}

	public void sendStatus() {
		this.sendStatus(-1);
	}

	public void sendStatus(int offlineUserID) {
		friends.stream()
			.filter(MessengerUser::isOnline)
			.map(MessengerUser::getPlayer)
			.map(Player::getMessenger)
			.filter(Messenger::hasInitalised)
			.forEach(messenger -> messenger.sendFriends(offlineUserID));
	}

	public void dispose() {
		this.sendStatus(this.player.getDetails().getId());

		Optional.ofNullable(this.friends).ifPresent(List::clear);
		Optional.ofNullable(this.requests).ifPresent(List::clear);
		
		this.friends = null;
		this.requests = null;
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

	public String getLocation() {
		return Stream.of(
			Optional.ofNullable(player.getPrivateRoomPlayer())
				.map(p -> p.getRoomUser().getRoom()),
			Optional.ofNullable(player.getPublicRoomPlayer())
				.map(p -> p.getRoomUser().getRoom())
		)
		.filter(Optional::isPresent)
		.map(Optional::get)
		.findFirst()
		.map(room -> {
			if (room.getData().getRoomType() == RoomType.PRIVATE) {
				return "In a user flat";
			}
			if (room.getData().getRoomType() == RoomType.PUBLIC) {
				return room.getData().getName();
			}
			return "On Hotel View";
		})
		.orElse("On Hotel View");
	}
}
