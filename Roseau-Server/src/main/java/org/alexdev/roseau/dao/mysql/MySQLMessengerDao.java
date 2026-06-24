package org.alexdev.roseau.dao.mysql;

import java.util.List;

import org.alexdev.roseau.dao.MessengerDao;
import org.alexdev.roseau.dao.mysql.entity.MessengerFriendshipEntity;
import org.alexdev.roseau.dao.mysql.entity.MessengerMessageEntity;
import org.alexdev.roseau.dao.mysql.entity.MessengerRequestEntity;
import org.alexdev.roseau.dao.mysql.mapper.EntityMapper;
import org.alexdev.roseau.game.messenger.MessengerMessage;
import org.alexdev.roseau.game.messenger.MessengerUser;
import org.alexdev.roseau.log.DateTime;
import org.alexdev.roseau.log.Log;
import org.oldskooler.entity4j.DbContext;

import com.google.common.collect.Lists;

public class MySQLMessengerDao implements MessengerDao {

	private MySQLDao dao;

	public MySQLMessengerDao(MySQLDao dao) {
		this.dao = dao;
	}

	@Override
	public List<MessengerUser> getFriends(int userId) {
		List<MessengerUser> friends = Lists.newArrayList();

		try (DbContext context = this.dao.getStorage().context()) {
			for (MessengerFriendshipEntity entity : context.from(MessengerFriendshipEntity.class)
					.filter(f -> f.equals(MessengerFriendshipEntity::getSender, userId)
							.or()
							.equals(MessengerFriendshipEntity::getReceiver, userId))
					.toList()) {
				friends.add(new MessengerUser(entity.getSender() != userId ? entity.getSender() : entity.getReceiver()));
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return friends;
	}

	@Override
	public List<MessengerUser> getRequests(int userId) {
		List<MessengerUser> users = Lists.newArrayList();

		try (DbContext context = this.dao.getStorage().context()) {
			for (MessengerRequestEntity entity : context.from(MessengerRequestEntity.class)
					.filter(f -> f.equals(MessengerRequestEntity::getToId, userId))
					.toList()) {
				users.add(new MessengerUser(entity.getFromId()));
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return users;
	}

	@Override
	public boolean newRequest(int fromId, int toId) {
		if (this.requestExists(fromId, toId)) {
			return false;
		}

		MessengerRequestEntity entity = new MessengerRequestEntity();
		entity.setFromId(fromId);
		entity.setToId(toId);

		try (DbContext context = this.dao.getStorage().context()) {
			context.insert(entity);
			return true;
		} catch (Exception e) {
			Log.exception(e);
			return false;
		}
	}

	@Override
	public boolean requestExists(int fromId, int toId) {
		try (DbContext context = this.dao.getStorage().context()) {
			return context.from(MessengerRequestEntity.class)
					.filter(f -> f.open()
							.equals(MessengerRequestEntity::getToId, toId)
							.and()
							.equals(MessengerRequestEntity::getFromId, fromId)
							.close()
							.or()
							.open()
							.equals(MessengerRequestEntity::getFromId, toId)
							.and()
							.equals(MessengerRequestEntity::getToId, fromId)
							.close())
					.first()
					.isPresent();
		} catch (Exception e) {
			Log.exception(e);
			return false;
		}
	}

	@Override
	public void removeRequest(int fromId, int toId) {
		try (DbContext context = this.dao.getStorage().context()) {
			context.from(MessengerRequestEntity.class)
					.filter(f -> f.equals(MessengerRequestEntity::getFromId, fromId)
							.and()
							.equals(MessengerRequestEntity::getToId, toId))
					.delete();
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	@Override
	public void removeFriend(int friendId, int userId) {
		try (DbContext context = this.dao.getStorage().context()) {
			context.from(MessengerFriendshipEntity.class)
					.filter(f -> f.open()
							.equals(MessengerFriendshipEntity::getSender, userId)
							.and()
							.equals(MessengerFriendshipEntity::getReceiver, friendId)
							.close()
							.or()
							.open()
							.equals(MessengerFriendshipEntity::getReceiver, userId)
							.and()
							.equals(MessengerFriendshipEntity::getSender, friendId)
							.close())
					.delete();
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	@Override
	public boolean newFriend(int sender, int receiver) {
		MessengerFriendshipEntity entity = new MessengerFriendshipEntity();
		entity.setSender(sender);
		entity.setReceiver(receiver);

		try (DbContext context = this.dao.getStorage().context()) {
			context.insert(entity);
			return true;
		} catch (Exception e) {
			Log.exception(e);
			return false;
		}
	}

	@Override
	public int newMessage(int fromID, int toID, String message) {
		MessengerMessageEntity entity = new MessengerMessageEntity();
		entity.setFromId(fromID);
		entity.setToId(toID);
		entity.setTimeSent(DateTime.getTime());
		entity.setMessage(message);
		entity.setUnread(1);

		try (DbContext context = this.dao.getStorage().context()) {
			context.insert(entity);
			return entity.getId();
		} catch (Exception e) {
			Log.exception(e);
			return 0;
		}
	}

	@Override
	public List<MessengerMessage> getUnreadMessages(int userId) {
		List<MessengerMessage> messages = Lists.newArrayList();

		try (DbContext context = this.dao.getStorage().context()) {
			for (MessengerMessageEntity entity : context.from(MessengerMessageEntity.class)
					.filter(f -> f.equals(MessengerMessageEntity::getToId, userId)
							.and()
							.equals(MessengerMessageEntity::getUnread, 1))
					.toList()) {
				messages.add(EntityMapper.toMessengerMessage(entity));
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return messages;
	}

	@Override
	public void markMessageRead(int messageID) {
		try (DbContext context = this.dao.getStorage().context()) {
			context.from(MessengerMessageEntity.class)
					.filter(f -> f.equals(MessengerMessageEntity::getId, messageID))
					.update(s -> s.set(MessengerMessageEntity::getUnread, 0));
		} catch (Exception e) {
			Log.exception(e);
		}
	}
}
