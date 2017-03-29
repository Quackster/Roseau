package org.alexdev.roseau.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.dao.IPlayerDao;
import org.alexdev.roseau.dao.util.IProcessStorage;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.log.Log;

public class MySQLPlayerDao extends IProcessStorage<PlayerDetails, ResultSet> implements IPlayerDao {

	private MySQLDao dao;

	public MySQLPlayerDao(MySQLDao dao) {
		this.dao = dao;
	}

	@Override
	public PlayerDetails getDetails(int userId) {

		Player player = Roseau.getGame().getPlayerManager().findById(userId);
		PlayerDetails details = new PlayerDetails(player);
		
		if (player != null) {
			details = player.getDetails();
		} else {

			Connection sqlConnection = null;
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;

			try {

				sqlConnection = this.dao.getStorage().getConnection();
				
				preparedStatement = this.dao.getStorage().prepare("SELECT id, username, rank, sso_ticket, motto, figure, credits FROM users WHERE id = ? LIMIT 1", sqlConnection);
				preparedStatement.setInt(1, userId);
				
				resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					this.fill(details, resultSet);
				}

			} catch (Exception e) {
				Log.exception(e);
			} finally {
				Storage.closeSilently(resultSet);
				Storage.closeSilently(preparedStatement);
				Storage.closeSilently(sqlConnection);
			}
		}

		return details;
	}

	@Override
	public boolean login(Player player, String ssoTicket) {
		
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT id, username, rank, sso_ticket, motto, figure, credits FROM users WHERE sso_ticket = ? LIMIT 1", sqlConnection);
			preparedStatement.setString(1, ssoTicket);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				this.fill(player.getDetails(), resultSet);
				return true;
			}

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return false;
	}

	@Override
	public int getId(String username) {

		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT id FROM users WHERE username = ? LIMIT 1", sqlConnection);
			preparedStatement.setString(1, username);
			
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				return resultSet.getInt("id");
			}
		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return -1;	
	}

	@Override
	public PlayerDetails fill(PlayerDetails details, ResultSet row) throws SQLException {
		details.fill(row.getInt("id"), row.getString("username"), row.getString("motto"),  row.getString("figure"), row.getInt("rank"), row.getInt("credits"));
		return details;
	}
}
