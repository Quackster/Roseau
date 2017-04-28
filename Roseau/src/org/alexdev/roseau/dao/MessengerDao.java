package org.alexdev.roseau.dao;

import java.util.List;

import org.alexdev.roseau.game.messenger.MessengerUser;

public interface MessengerDao {

	public List<MessengerUser> getFriends(int userId);
	public List<MessengerUser> getRequests(int userId);
	public boolean newRequest(int fromId, int toId);
	public boolean removeRequest(int fromId, int toId);
	public boolean newFriend(int sender, int receiver);
	public boolean removeFriend(int friendId, int userId);
	boolean requestExists(int fromId, int toId);
}