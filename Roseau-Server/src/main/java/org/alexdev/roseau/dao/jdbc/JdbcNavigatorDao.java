package org.alexdev.roseau.dao.jdbc;

import org.oldskooler.simplelogger4j.SimpleLog;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.dao.NavigatorDao;
import org.alexdev.roseau.dao.util.IProcessStorage;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.settings.RoomType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcNavigatorDao extends IProcessStorage<Room, ResultSet> implements NavigatorDao {

    private static final SimpleLog logger = SimpleLog.of(JdbcNavigatorDao.class);
    
	private final JdbcDao dao;

	public JdbcNavigatorDao(JdbcDao dao) {
		this.dao = dao;
	}

	@Override
	public List<Room> getRoomsByLikeName(String name) {
		String sql = "SELECT * FROM rooms WHERE name LIKE ? AND room_type = 0";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, "%" + name + "%");
			
			try (ResultSet resultSet = statement.executeQuery()) {
				List<Room> rooms = new ArrayList<>();
				while (resultSet.next()) {
					int id = resultSet.getInt("id");
					
					Room room = Optional.ofNullable(Roseau.getGame().getRoomManager().getRoomByID(id))
						.orElseGet(() -> {
							try {
								return fill(resultSet);
							} catch (Exception e) {
								logger.error("Error filling room", e);
								return null;
							}
						});
					
					Optional.ofNullable(room).ifPresent(rooms::add);
				}
				return rooms;
			}
			
		} catch (SQLException e) {
			logger.error("Failed to get rooms by name: " + name, e);
			return new ArrayList<>();
		}
	}

	@Override
	public Room fill(ResultSet row) throws Exception {
		RoomType type = RoomType.getType(row.getInt("room_type"));
		
		PlayerDetails details = Optional.of(type)
			.filter(t -> t == RoomType.PRIVATE)
			.map(t -> {
				try {
					return Roseau.getDao().getPlayer().getDetails(row.getInt("owner_id"));
				} catch (SQLException e) {
					logger.error("Error getting owner details", e);
					return null;
				}
			})
			.orElse(null);
		
		Room instance = new Room();
		
		instance.getData().fill(
			row.getInt("id"),
			row.getInt("hidden") == 1,
			type,
			Optional.ofNullable(details).map(PlayerDetails::getId).orElse(0),
			Optional.ofNullable(details).map(PlayerDetails::getName).orElse(""),
			row.getString("name"),
			row.getInt("state"),
			row.getString("password"),
			row.getInt("users_now"),
			row.getInt("users_max"),
			row.getString("description"),
			row.getString("model"),
			row.getString("cct"),
			row.getString("wallpaper"),
			row.getString("floor"),
			false,
			row.getInt("show_owner_name") == 1
		);
		
		Optional.ofNullable(details)
			.ifPresent(d -> instance.getData().setOwnerName(d.getName()));
		
		instance.load();
		
		return instance;
	}
}
