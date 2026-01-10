package org.alexdev.roseau.dao.jdbc;

import org.oldskooler.simplelogger4j.SimpleLog;
import org.alexdev.roseau.dao.ItemDao;
import org.alexdev.roseau.dao.util.IProcessStorage;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.ItemDefinition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class JdbcItemDao extends IProcessStorage<Item, ResultSet> implements ItemDao {

    private static final SimpleLog logger = SimpleLog.of(JdbcItemDao.class);
    
	private final JdbcDao dao;

	public JdbcItemDao(JdbcDao dao) {
		this.dao = dao;
	}

	@Override
	public Map<Integer, ItemDefinition> getDefinitions() {
		String sql = "SELECT * FROM item_definitions";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {
			
			Map<Integer, ItemDefinition> definitions = new HashMap<>();
			while (resultSet.next()) {
				definitions.put(
					resultSet.getInt("id"),
					new ItemDefinition(
						resultSet.getInt("id"),
						resultSet.getString("sprite"),
						resultSet.getString("color"),
						resultSet.getInt("length"),
						resultSet.getInt("width"),
						resultSet.getDouble("height"),
						resultSet.getString("behaviour"),
						resultSet.getString("name"),
						resultSet.getString("description"),
						resultSet.getString("dataclass")
					)
				);
			}
			return definitions;
			
		} catch (SQLException e) {
			logger.error("Failed to get item definitions", e);
			return new HashMap<>();
		}
	}

	@Override
	public ConcurrentHashMap<Integer, Item> getPublicRoomItems(String model, int roomId) {
		String sql = "SELECT * FROM room_public_items WHERE model = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, model);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				ConcurrentHashMap<Integer, Item> items = new ConcurrentHashMap<>();
				while (resultSet.next()) {
					items.put(
						resultSet.getInt("id"),
						new Item(
							resultSet.getInt("id"),
							roomId,
							-1,
							resultSet.getString("x"),
							resultSet.getInt("y"),
							resultSet.getDouble("z"),
							resultSet.getInt("rotation"),
							resultSet.getInt("definitionid"),
							resultSet.getString("object"),
							resultSet.getString("data")
						)
					);
				}
				return items;
			}
			
		} catch (SQLException e) {
			logger.error("Failed to get public room items for model: " + model, e);
			return new ConcurrentHashMap<>();
		}
	}

	@Override
	public ConcurrentHashMap<Integer, Item> getRoomItems(int roomId) {
		String sql = "SELECT * FROM items WHERE room_id = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, roomId);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				ConcurrentHashMap<Integer, Item> items = new ConcurrentHashMap<>();
				while (resultSet.next()) {
					try {
						items.put(resultSet.getInt("id"), fill(resultSet));
					} catch (Exception e) {
						logger.error("Error filling item", e);
					}
				}
				return items;
			}
			
		} catch (SQLException e) {
			logger.error("Failed to get room items for room: " + roomId, e);
			return new ConcurrentHashMap<>();
		}
	}

	@Override
	public Item getItem(int itemId) {
		String sql = "SELECT * FROM items WHERE id = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, itemId);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				return Optional.ofNullable(resultSet.next() ? resultSet : null)
					.map(rs -> {
						try {
							return fill(rs);
						} catch (Exception e) {
							logger.error("Error filling item", e);
							return null;
						}
					})
					.orElse(null);
			}
			
		} catch (SQLException e) {
			logger.error("Failed to get item: " + itemId, e);
			return null;
		}
	}

	@Override
	public void saveItem(Item item) {
		String sql = """
			UPDATE items
			SET extra_data = ?, x = ?, y = ?, z = ?, rotation = ?, room_id = ?, user_id = ?
			WHERE id = ?
			""";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			if (item.getDefinition().getBehaviour().isTeleporter()) {
				try {
					Integer.valueOf(item.getCustomData());
					statement.setString(1, item.getCustomData());
				} catch (NumberFormatException e) {
					statement.setString(1, String.valueOf(item.getTargetTeleporterId()));
				}
			} else {
				statement.setString(1, item.getCustomData());
			}
			
			if (item.getDefinition().getBehaviour().isOnWall()) {
				statement.setString(2, item.getWallPosition());
			} else {
				statement.setInt(2, item.getPosition().getX());
			}
			
			statement.setInt(3, item.getPosition().getY());
			statement.setDouble(4, item.getPosition().getZ());
			statement.setInt(5, item.getPosition().getRotation());
			statement.setInt(6, item.getRoomId());
			statement.setLong(7, item.getOwnerId());
			statement.setLong(8, item.getId());
			
			statement.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Failed to save item: " + item.getId(), e);
		}
	}

	@Override
	public void deleteItem(long id) {
		String sql = "DELETE FROM items WHERE id = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setLong(1, id);
			statement.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Failed to delete item: " + id, e);
		}
	}

	@Override
	public Item fill(ResultSet row) throws Exception {
		return new Item(
			row.getInt("id"),
			row.getInt("room_id"),
			row.getInt("user_id"),
			row.getString("x"),
			row.getInt("y"),
			row.getDouble("z"),
			row.getInt("rotation"),
			row.getInt("item_id"),
			"",
			row.getString("extra_data")
		);
	}
}
