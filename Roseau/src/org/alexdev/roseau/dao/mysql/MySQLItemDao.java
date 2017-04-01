package org.alexdev.roseau.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alexdev.roseau.dao.ItemDao;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.ItemDefinition;
import org.alexdev.roseau.log.Log;

public class MySQLItemDao implements ItemDao {

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
						resultSet.getString("description")));
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
	public List<Item> getPublicRoomItems(String model) {
		
		List<Item> items = new ArrayList<Item>();
		
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT * FROM room_public_items WHERE model = ?", sqlConnection);
			preparedStatement.setString(1, model);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				items.add(new Item(resultSet.getInt("id"),
						"",
						resultSet.getInt("x"),
						resultSet.getInt("y"),
						resultSet.getDouble("z"),
						resultSet.getInt("rotation"),
						resultSet.getInt("definitionid")));
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
}
