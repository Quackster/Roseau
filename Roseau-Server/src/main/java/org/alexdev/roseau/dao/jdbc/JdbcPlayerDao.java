package org.alexdev.roseau.dao.jdbc;

import org.oldskooler.simplelogger4j.SimpleLog;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.dao.PlayerDao;
import org.alexdev.roseau.dao.util.IProcessStorage;
import org.alexdev.roseau.game.GameVariables;
import org.alexdev.roseau.game.player.Permission;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.log.DateTime;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class JdbcPlayerDao extends IProcessStorage<PlayerDetails, ResultSet> implements PlayerDao {

    private static final SimpleLog logger = SimpleLog.of(JdbcPlayerDao.class);
    
	private final JdbcDao dao;
	private static final String FIELDS = "id, username, password, rank, mission, figure, pool_figure, email, credits, sex, country, badge, birthday, last_online, personal_greeting, tickets";

	public JdbcPlayerDao(JdbcDao dao) {
		this.dao = dao;
	}

	@Override
	public void createPlayer(String username, String password, String email, String mission, String figure, int credits, String sex, String birthday) {
		String sql = """
			INSERT INTO users (username, password, email, mission, figure, credits, sex, birthday, join_date, last_online, personal_greeting)
			VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
			""";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
			long currentTime = DateTime.getTime();
			statement.setString(1, username);
			statement.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
			statement.setString(3, email);
			statement.setString(4, mission);
			statement.setString(5, figure);
			statement.setInt(6, GameVariables.USER_DEFAULT_CREDITS);
			statement.setString(7, sex);
			statement.setString(8, birthday);
			statement.setLong(9, currentTime);
			statement.setLong(10, currentTime);
			statement.setString(11, GameVariables.MESSENGER_GREETING);
			
			statement.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Failed to create player: " + username, e);
		}
	}

	@Override
	public PlayerDetails getDetails(int userId) {
		String sql = "SELECT " + FIELDS + " FROM users WHERE id = ? LIMIT 1";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, userId);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				return Optional.ofNullable(resultSet.next() ? resultSet : null)
					.map(rs -> {
						try {
							PlayerDetails details = new PlayerDetails(null);
							return fill(details, rs);
						} catch (SQLException e) {
							logger.error("Error filling player details", e);
							return null;
						}
					})
					.orElse(null);
			}
			
		} catch (SQLException e) {
			logger.error("Failed to get player details for user ID: " + userId, e);
			return null;
		}
	}

	@Override
	public PlayerDetails getDetails(String username) {
		return Optional.ofNullable(Roseau.getGame().getPlayerManager().getByName(username))
			.map(Player::getDetails)
			.orElseGet(() -> {
				String sql = "SELECT " + FIELDS + " FROM users WHERE username = ? LIMIT 1";
				
				try (Connection connection = dao.getStorage().getConnection();
					 PreparedStatement statement = connection.prepareStatement(sql)) {
					
					statement.setString(1, username);
					
					try (ResultSet resultSet = statement.executeQuery()) {
						return Optional.ofNullable(resultSet.next() ? resultSet : null)
							.map(rs -> {
								try {
									return fill(new PlayerDetails(null), rs);
								} catch (SQLException e) {
									logger.error("Error filling player details", e);
									return null;
								}
							})
							.orElse(null);
					}
					
				} catch (SQLException e) {
					logger.error("Failed to get player details for username: " + username, e);
					return null;
				}
			});
	}

	@Override
	public boolean login(Player player, String username, String password) {
		String sql = "SELECT " + FIELDS + " FROM users WHERE username = ? LIMIT 1";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, username);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				return Optional.ofNullable(resultSet.next() ? resultSet : null)
					.filter(rs -> {
						try {
							String storedPassword = rs.getString("password");
							return BCrypt.checkpw(password, storedPassword);
						} catch (SQLException e) {
							logger.error("Error checking password", e);
							return false;
						}
					})
					.map(rs -> {
						try {
							fill(player.getDetails(), rs);
							return true;
						} catch (SQLException e) {
							logger.error("Error filling player details during login", e);
							return false;
						}
					})
					.orElse(false);
			}
			
		} catch (SQLException e) {
			logger.error("Failed to login user: " + username, e);
			return false;
		}
	}

	@Override
	public int getId(String username) {
		String sql = "SELECT id FROM users WHERE username = ? LIMIT 1";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, username);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				return Optional.ofNullable(resultSet.next() ? resultSet : null)
					.map(rs -> {
						try {
							return rs.getInt("id");
						} catch (SQLException e) {
							logger.error("Error getting user ID", e);
							return -1;
						}
					})
					.orElse(-1);
			}
			
		} catch (SQLException e) {
			logger.error("Failed to get user ID for username: " + username, e);
			return -1;
		}
	}

	@Override
	public boolean isNameTaken(String name) {
		String sql = "SELECT id FROM users WHERE username = ? LIMIT 1";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, name);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next();
			}
			
		} catch (SQLException e) {
			logger.error("Failed to check if name is taken: " + name, e);
			return false;
		}
	}

	@Override
	public void updatePlayer(PlayerDetails details) {
		String sql = """
			UPDATE users
			SET password = ?, figure = ?, credits = ?, mission = ?, pool_figure = ?, sex = ?, email = ?, personal_greeting = ?, tickets = ?
			WHERE id = ?
			""";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, BCrypt.hashpw(details.getPassword(), BCrypt.gensalt()));
			statement.setString(2, details.getFigure());
			statement.setInt(3, details.getCredits());
			statement.setString(4, details.getMission());
			statement.setString(5, details.getPoolFigure());
			statement.setString(6, details.getSex());
			statement.setString(7, details.getEmail());
			statement.setString(8, details.getPersonalGreeting());
			statement.setInt(9, details.getTickets());
			statement.setInt(10, details.getId());
			
			statement.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Failed to update player: " + details.getId(), e);
		}
	}

	@Override
	public void updateLastLogin(PlayerDetails details) {
		String sql = "UPDATE users SET last_online = ? WHERE id = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setLong(1, DateTime.getTime());
			statement.setInt(2, details.getId());
			
			statement.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Failed to update last login for user: " + details.getId(), e);
		}
	}

	@Override
	public List<Permission> getPermissions() {
		String sql = "SELECT * FROM users_permissions";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {
			
			List<Permission> permissions = new ArrayList<>();
			while (resultSet.next()) {
				int rank = resultSet.getInt("rank");
				String permission = resultSet.getString("permission");
				boolean inheritable = resultSet.getByte("inheritable") == 1;
				
				permissions.add(new Permission(permission, inheritable, rank));
			}
			return permissions;
			
		} catch (SQLException e) {
			logger.error("Failed to get permissions", e);
			return new ArrayList<>();
		}
	}

	@Override
	public PlayerDetails fill(PlayerDetails details, ResultSet row) throws SQLException {
		details.fill(
			row.getInt("id"),
			row.getString("username"),
			row.getString("mission"),
			row.getString("figure"),
			row.getString("pool_figure"),
			row.getString("email"),
			row.getInt("rank"),
			row.getInt("credits"),
			row.getString("sex"),
			row.getString("country"),
			row.getString("badge"),
			row.getString("birthday"),
			row.getLong("last_online"),
			row.getString("personal_greeting"),
			row.getInt("tickets")
		);
		return details;
	}
}
