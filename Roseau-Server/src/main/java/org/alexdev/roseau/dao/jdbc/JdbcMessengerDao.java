package org.alexdev.roseau.dao.jdbc;

import org.oldskooler.simplelogger4j.SimpleLog;
import org.alexdev.roseau.dao.MessengerDao;
import org.alexdev.roseau.game.messenger.MessengerMessage;
import org.alexdev.roseau.game.messenger.MessengerUser;
import org.alexdev.roseau.log.DateTime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMessengerDao implements MessengerDao {

    private static final SimpleLog logger = SimpleLog.of(JdbcMessengerDao.class);
    
	private final JdbcDao dao;

	public JdbcMessengerDao(JdbcDao dao) {
		this.dao = dao;
	}

	@Override
	public List<MessengerUser> getFriends(int userId) {
		String sql = "SELECT * FROM messenger_friendships WHERE sender = ? OR receiver = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, userId);
			statement.setInt(2, userId);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				List<MessengerUser> friends = new ArrayList<>();
				while (resultSet.next()) {
					int sender = resultSet.getInt("sender");
					int receiver = resultSet.getInt("receiver");
					
					int friendId = (sender != userId) ? sender : receiver;
					friends.add(new MessengerUser(friendId));
				}
				return friends;
			}
			
		} catch (SQLException e) {
			logger.error("Failed to get friends for user: " + userId, e);
			return new ArrayList<>();
		}
	}

	@Override
	public List<MessengerUser> getRequests(int userId) {
		String sql = "SELECT from_id FROM messenger_requests WHERE to_id = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, userId);
			
			try (ResultSet rs = statement.executeQuery()) {
				List<MessengerUser> users = new ArrayList<>();
				while (rs.next()) {
					users.add(new MessengerUser(rs.getInt("from_id")));
				}
				return users;
			}
			
		} catch (SQLException e) {
			logger.error("Failed to get requests for user: " + userId, e);
			return new ArrayList<>();
		}
	}

	@Override
	public boolean newRequest(int fromId, int toId) {
		if (requestExists(fromId, toId)) {
			return false;
		}
		
		String sql = "INSERT INTO messenger_requests (to_id, from_id) VALUES (?, ?)";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, toId);
			statement.setInt(2, fromId);
			statement.execute();
			
			return true;
			
		} catch (SQLException e) {
			logger.error("Failed to create friend request from " + fromId + " to " + toId, e);
			return false;
		}
	}

	@Override
	public boolean requestExists(int fromId, int toId) {
		String sql = """
			SELECT * FROM messenger_requests
			WHERE (to_id = ? AND from_id = ?) OR (from_id = ? AND to_id = ?)
			""";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, toId);
			statement.setInt(2, fromId);
			statement.setInt(3, toId);
			statement.setInt(4, fromId);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next();
			}
			
		} catch (SQLException e) {
			logger.error("Failed to check if request exists from " + fromId + " to " + toId, e);
			return false;
		}
	}

	@Override
	public void removeRequest(int fromId, int toId) {
		String sql = "DELETE FROM messenger_requests WHERE from_id = ? AND to_id = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, fromId);
			statement.setInt(2, toId);
			statement.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Failed to remove request from " + fromId + " to " + toId, e);
		}
	}

	@Override
	public void removeFriend(int friendId, int userId) {
		String sql = """
			DELETE FROM messenger_friendships
			WHERE (sender = ? AND receiver = ?) OR (receiver = ? AND sender = ?)
			""";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, userId);
			statement.setInt(2, friendId);
			statement.setInt(3, userId);
			statement.setInt(4, friendId);
			statement.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Failed to remove friend " + friendId + " from user " + userId, e);
		}
	}

	@Override
	public boolean newFriend(int sender, int receiver) {
		String sql = "INSERT INTO messenger_friendships (sender, receiver) VALUES (?, ?)";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, sender);
			statement.setInt(2, receiver);
			statement.executeUpdate();
			
			return true;
			
		} catch (SQLException e) {
			logger.error("Failed to create friendship from " + sender + " to " + receiver, e);
			return false;
		}
	}

	@Override
	public int newMessage(int fromId, int toId, String message) {
		String sql = "INSERT INTO messenger_messages (from_id, to_id, time_sent, message) VALUES (?, ?, ?, ?)";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
			statement.setInt(1, fromId);
			statement.setInt(2, toId);
			statement.setLong(3, DateTime.getTime());
			statement.setString(4, message);
			statement.executeUpdate();
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				return Optional.ofNullable(generatedKeys.next() ? generatedKeys.getInt(1) : null)
					.orElse(0);
			}
			
		} catch (SQLException e) {
			logger.error("Failed to create message from " + fromId + " to " + toId, e);
			return 0;
		}
	}

	@Override
	public List<MessengerMessage> getUnreadMessages(int userId) {
		String sql = "SELECT * FROM messenger_messages WHERE to_id = ? AND unread = 1";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, userId);
			
			try (ResultSet rs = statement.executeQuery()) {
				List<MessengerMessage> messages = new ArrayList<>();
				while (rs.next()) {
					messages.add(new MessengerMessage(
						rs.getInt("id"),
						rs.getInt("to_id"),
						rs.getInt("from_id"),
						rs.getLong("time_sent"),
						rs.getString("message")
					));
				}
				return messages;
			}
			
		} catch (SQLException e) {
			logger.error("Failed to get unread messages for user: " + userId, e);
			return new ArrayList<>();
		}
	}

	@Override
	public void markMessageRead(int messageId) {
		String sql = "UPDATE messenger_messages SET unread = 0 WHERE id = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, messageId);
			statement.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Failed to mark message as read: " + messageId, e);
		}
	}

	public JdbcDao getDao() {
		return dao;
	}
}
