package org.alexdev.roseau.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.alexdev.roseau.dao.InventoryDao;
import org.alexdev.roseau.dao.util.IProcessStorage;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.log.Log;

import com.google.common.collect.Lists;

public class MySQLInventoryDao extends IProcessStorage<Item, ResultSet> implements InventoryDao {

	private MySQLDao dao;

	public MySQLInventoryDao(MySQLDao dao) {
		this.dao = dao;
	}

	@Override
	public List<Item> getInventoryItems(int userId) {

		List<Item> items = Lists.newArrayList();
		
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			
			preparedStatement = this.dao.getStorage().prepare("SELECT id, user_id, item_id, room_id, x, y, z, rotation, extra_data FROM items WHERE room_id = 0 AND user_id = ?", sqlConnection);
			preparedStatement.setInt(1, userId);
			
			resultSet = preparedStatement.executeQuery();
	
			while (resultSet.next()) {
				items.add(this.fill(resultSet));
			}

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}
		
		return items;
	}

	@Override
	public Item getItem(long id) {

		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			
			preparedStatement = this.dao.getStorage().prepare("SELECT id, user_id, item_id, room_id, x, y, z, rotation, extra_data FROM items WHERE id = ? LIMIT 1", sqlConnection);
			preparedStatement.setLong(1, id);
			
			resultSet = preparedStatement.executeQuery();
				
			if (!resultSet.next()) {
				return null;
			}

			return this.fill(resultSet);
			
		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return null;
	}

	@Override
	public Item newItem(int itemId, int ownerId, String extraData) {

		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Item item = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("INSERT INTO items (user_id, item_id, extra_data) VALUES(?, ?, ?)", sqlConnection);
			
			preparedStatement.setInt(1, ownerId);
			preparedStatement.setInt(2, itemId);
			preparedStatement.setString(3, extraData);
			preparedStatement.executeUpdate();

			resultSet = preparedStatement.getGeneratedKeys();

			if (resultSet != null && resultSet.next()) {
				item = this.getItem(resultSet.getLong(1));
			}

		} catch (SQLException e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return item;
	}

	@Override
	public Item fill(ResultSet row) throws Exception {
		Item item = new Item(row.getInt("id"), row.getInt("room_id"), row.getInt("user_id"), row.getInt("x"), row.getInt("y"), row.getDouble("z"), row.getInt("rotation"), row.getInt("item_id"), "", "", row.getString("extra_data"));
		return item;
	}

}
