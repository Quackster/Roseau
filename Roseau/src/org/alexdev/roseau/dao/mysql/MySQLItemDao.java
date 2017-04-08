package org.alexdev.roseau.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.alexdev.roseau.dao.ItemDao;
import org.alexdev.roseau.dao.util.IProcessStorage;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.ItemDefinition;
import org.alexdev.roseau.log.Log;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MySQLItemDao extends IProcessStorage<Item, ResultSet> implements ItemDao {

	private MySQLDao dao;
	
	public MySQLItemDao(MySQLDao dao) {
		this.dao = dao;
	}

	@Override
	public Map<Integer, ItemDefinition> getDefinitions() {
		
		Map<Integer, ItemDefinition> definitions = new HashMap<Integer, ItemDefinition>();
		
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT * FROM item_definitions", sqlConnection);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				definitions.put(resultSet.getInt("id"), new ItemDefinition(
						resultSet.getInt("id"), 
						resultSet.getString("sprite"), 
						resultSet.getString("color"), 
						resultSet.getInt("length"),
						resultSet.getInt("width"),
						resultSet.getInt("height"),
						resultSet.getString("behaviour"),
						resultSet.getString("name"),
						resultSet.getString("description"),
						resultSet.getString("dataclass")));
			}

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}
		
		
		return definitions;
	}

	@Override
	public ConcurrentHashMap<Integer, Item> getPublicRoomItems(String model) {
		
		ConcurrentHashMap<Integer, Item> items = new ConcurrentHashMap<Integer, Item>();
		
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT * FROM room_public_items WHERE model = ?", sqlConnection);
			preparedStatement.setString(1, model);
			resultSet = preparedStatement.executeQuery();

			/*	public Item(int id, int roomId, int ownerId, int x, int y, double z, int rotation, int definition, String itemData, String customData, String extraData) {
			 * 
			 * ;*/
			
			while (resultSet.next()) {
				items.put(resultSet.getInt("id"), new Item(resultSet.getInt("id"),
						-1,
						-1,
						resultSet.getInt("x"),
						resultSet.getInt("y"),
						resultSet.getDouble("z"),
						resultSet.getInt("rotation"),
						resultSet.getInt("definitionid"),
						resultSet.getString("object"),
						resultSet.getString("data"),
						""));
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
	public ConcurrentHashMap<Integer, Item> getRoomItems(int roomId) {

		ConcurrentHashMap<Integer, Item> items = new ConcurrentHashMap<Integer, Item>();

		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT * FROM items WHERE room_id = " + roomId, sqlConnection);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				items.put(resultSet.getInt("id"), this.fill(resultSet));
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
	public void saveItem(Item item) {

		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			
			preparedStatement = this.dao.getStorage().prepare("UPDATE items SET extra_data = ?, x = ?, y = ?, z = ?, rotation = ?, room_id = ? WHERE id = ?", sqlConnection);
			preparedStatement.setString(1, "");
			preparedStatement.setInt(2, item.getX());
			preparedStatement.setInt(3, item.getY());
			preparedStatement.setDouble(4, item.getZ());
			preparedStatement.setInt(5, item.getRotation());
			preparedStatement.setInt(6, item.getRoomId());
			preparedStatement.setLong(7, item.getId());
			preparedStatement.executeUpdate();

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}
	}

	@Override
	public void deleteItem(long id) {
		this.dao.getStorage().execute("DELETE FROM items WHERE id = " + id);
	}

	
	@Override
	public Item fill(ResultSet row) throws Exception {
		
		//public Item(int id, int roomId, int ownerId, int x, int y, double z, int rotation, int definition, String itemData, String customData, String extraData) {
		
		Item item = new Item(row.getInt("id"), row.getInt("room_id"), row.getInt("user_id"), row.getInt("x"), row.getInt("y"), row.getDouble("z"), row.getInt("rotation"), row.getInt("item_id"), "", "", row.getString("extra_data"));
		return item;
	}
}
