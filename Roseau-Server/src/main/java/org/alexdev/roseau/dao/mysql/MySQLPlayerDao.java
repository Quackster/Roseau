package org.alexdev.roseau.dao.mysql;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.dao.PlayerDao;
import org.alexdev.roseau.dao.mysql.entity.UserEntity;
import org.alexdev.roseau.dao.mysql.entity.UserPermissionEntity;
import org.alexdev.roseau.dao.mysql.mapper.EntityMapper;
import org.alexdev.roseau.game.GameVariables;
import org.alexdev.roseau.game.player.Permission;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.log.DateTime;
import org.alexdev.roseau.log.Log;
import org.mindrot.jbcrypt.BCrypt;
import org.oldskooler.entity4j.DbContext;

import com.google.common.collect.Lists;

public class MySQLPlayerDao implements PlayerDao {

	private MySQLDao dao;

	public MySQLPlayerDao(MySQLDao dao) {
		this.dao = dao;
	}

	@Override
	public void createPlayer(String username, String password, String email, String mission, String figure, int credits, String sex, String birthday) {
		UserEntity entity = new UserEntity();
		entity.setUsername(username);
		entity.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
		entity.setEmail(email);
		entity.setMission(mission);
		entity.setFigure(figure);
		entity.setCredits(GameVariables.USER_DEFAULT_CREDITS);
		entity.setSex(sex);
		entity.setBirthday(birthday);
		entity.setJoinDate(DateTime.getTime());
		entity.setLastOnline(DateTime.getTime());
		entity.setPersonalGreeting(GameVariables.MESSENGER_GREETING);

		try (DbContext context = this.dao.getStorage().context()) {
			context.insert(entity);
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	@Override
	public PlayerDetails getDetails(int userID) {
		try (DbContext context = this.dao.getStorage().context()) {
			return context.from(UserEntity.class)
					.filter(f -> f.equals(UserEntity::getId, userID))
					.first()
					.map(entity -> EntityMapper.toPlayerDetails(entity, new PlayerDetails(null)))
					.orElse(null);
		} catch (Exception e) {
			Log.exception(e);
			return null;
		}
	}

	@Override
	public PlayerDetails getDetails(String username) {
		Player player = Roseau.getGame().getPlayerManager().getByName(username);

		if (player != null) {
			return player.getDetails();
		}

		try (DbContext context = this.dao.getStorage().context()) {
			return context.from(UserEntity.class)
					.filter(f -> f.equals(UserEntity::getUsername, username))
					.first()
					.map(entity -> EntityMapper.toPlayerDetails(entity, new PlayerDetails(player)))
					.orElse(null);
		} catch (Exception e) {
			Log.exception(e);
			return null;
		}
	}

	@Override
	public boolean login(Player player, String username, String password) {
		try (DbContext context = this.dao.getStorage().context()) {
			UserEntity entity = context.from(UserEntity.class)
					.filter(f -> f.equals(UserEntity::getUsername, username))
					.first()
					.orElse(null);

			if (entity != null && BCrypt.checkpw(password, entity.getPassword())) {
				EntityMapper.toPlayerDetails(entity, player.getDetails());
				return true;
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return false;
	}

	@Override
	public int getId(String username) {
		try (DbContext context = this.dao.getStorage().context()) {
			UserEntity entity = context.from(UserEntity.class)
					.filter(f -> f.equals(UserEntity::getUsername, username))
					.first()
					.orElse(null);
			return entity == null ? -1 : entity.getId();
		} catch (Exception e) {
			Log.exception(e);
			return -1;
		}
	}

	@Override
	public boolean isNameTaken(String name) {
		try (DbContext context = this.dao.getStorage().context()) {
			return context.from(UserEntity.class)
					.filter(f -> f.equals(UserEntity::getUsername, name))
					.first()
					.isPresent();
		} catch (Exception e) {
			Log.exception(e);
			return false;
		}
	}

	@Override
	public void updatePlayer(PlayerDetails details) {
		try (DbContext context = this.dao.getStorage().context()) {
			UserEntity entity = context.from(UserEntity.class)
					.filter(f -> f.equals(UserEntity::getId, details.getID()))
					.first()
					.orElse(null);

			if (entity == null) {
				return;
			}

			entity.setPassword(BCrypt.hashpw(details.getPassword(), BCrypt.gensalt()));
			entity.setFigure(details.getFigure());
			entity.setCredits(details.getCredits());
			entity.setMission(details.getMission());
			entity.setPoolFigure(details.getPoolFigure());
			entity.setSex(details.getSex());
			entity.setEmail(details.getEmail());
			entity.setPersonalGreeting(details.getPersonalGreeting());
			entity.setTickets(details.getTickets());
			context.update(entity);
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	@Override
	public void updateLastLogin(PlayerDetails details) {
		try (DbContext context = this.dao.getStorage().context()) {
			context.from(UserEntity.class)
					.filter(f -> f.equals(UserEntity::getId, details.getID()))
					.update(s -> s.set(UserEntity::getLastOnline, DateTime.getTime()));
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	@Override
	public List<Permission> getPermissions() {
		List<Permission> permissions = Lists.newArrayList();

		try (DbContext context = this.dao.getStorage().context()) {
			for (UserPermissionEntity entity : context.from(UserPermissionEntity.class).toList()) {
				permissions.add(new Permission(entity.getPermission(), entity.getInheritable() == 1, entity.getRank()));
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return permissions;
	}
}
