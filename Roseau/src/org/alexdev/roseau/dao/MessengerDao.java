package org.alexdev.roseau.dao;

import java.util.List;

import org.alexdev.roseau.game.messenger.MessengerMessage;
import org.alexdev.roseau.game.messenger.MessengerUser;

public interface MessengerDao {

	public List<MessengerUser> getFriends(int userId);
	public List<MessengerUser> getRequests(int userId);
	public boolean newRequest(int fromId, int toId);
	public boolean removeRequest(int fromId, int toId);
	public boolean newFriend(int sender, int receiver);
	public boolean removeFriend(int friendId, int userId);
	public boolean requestExists(int fromId, int toId);
	public int newMessage(int fromID, int toID, String message);
	public List<MessengerMessage> getUnreadMessages(int userId);
	public boolean markMessageRead(int messageID);
}