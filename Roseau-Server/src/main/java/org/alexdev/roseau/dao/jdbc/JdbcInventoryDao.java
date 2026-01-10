package org.alexdev.roseau.dao.jdbc;

import org.oldskooler.simplelogger4j.SimpleLog;
import org.alexdev.roseau.dao.InventoryDao;
import org.alexdev.roseau.dao.util.IProcessStorage;
import org.alexdev.roseau.game.item.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class JdbcInventoryDao extends IProcessStorage<Item, ResultSet> implements InventoryDao {

    private static final SimpleLog logger = SimpleLog.of(JdbcInventoryDao.class);
    
	private final JdbcDao dao;

	public JdbcInventoryDao(JdbcDao dao) {
		this.dao = dao;
	}

	@Override
	public List<Item> getInventoryItems(int userId) {
		String sql = "SELECT id, user_id, item_id, room_id, x, y, z, rotation, extra_data FROM items WHERE room_id = 0 AND user_id = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, userId);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				List<Item> items = new ArrayList<>();
				while (resultSet.next()) {
					try {
						items.add(fill(resultSet));
					} catch (Exception e) {
						logger.error("Error filling inventory item", e);
					}
				}
				return items;
			}
			
		} catch (SQLException e) {
			logger.error("Failed to get inventory items for user: " + userId, e);
			return new ArrayList<>();
		}
	}

	@Override
	public Item getItem(long id) {
		String sql = "SELECT id, user_id, item_id, room_id, x, y, z, rotation, extra_data FROM items WHERE id = ? LIMIT 1";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setLong(1, id);
			
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
			logger.error("Failed to get item: " + id, e);
			return null;
		}
	}

	@Override
	public Item newItem(int itemId, int ownerId, String extraData) {
		String sql = "INSERT INTO items (user_id, item_id, extra_data) VALUES(?, ?, ?)";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
			statement.setInt(1, ownerId);
			statement.setInt(2, itemId);
			statement.setString(3, extraData);
			statement.executeUpdate();
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				return Optional.ofNullable(generatedKeys.next() ? generatedKeys.getLong(1) : null)
					.map(newItemId -> getItem(newItemId))
					.orElse(null);
			}
			
		} catch (SQLException e) {
			logger.error("Failed to create new item", e);
			return null;
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
