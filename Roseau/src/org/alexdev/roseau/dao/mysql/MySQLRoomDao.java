package org.alexdev.roseau.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.dao.RoomDao;
import org.alexdev.roseau.dao.util.IProcessStorage;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.RoomData;
import org.alexdev.roseau.game.room.model.RoomModel;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.log.Log;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MySQLRoomDao extends IProcessStorage<Room, ResultSet> implements RoomDao {

	private MySQLDao dao;
	private Map<String, RoomModel> roomModels;

	public MySQLRoomDao(MySQLDao dao) {

		this.dao = dao;
		this.roomModels = Maps.newHashMap();

		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT * FROM room_models", sqlConnection);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				roomModels.put(resultSet.getString("id"), new RoomModel(resultSet.getString("id"), resultSet.getString("heightmap"), resultSet.getInt("door_x"), resultSet.getInt("door_y"), 
						resultSet.getInt("door_z"), resultSet.getInt("door_dir"), resultSet.getByte("has_pool") == 1));
			}

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}
	}


	@Override
	public List<Room> getPlayerRooms(PlayerDetails details) {
		return getPlayerRooms(details, false);
	}

	@Override
	public List<Room> getPublicRooms(boolean storeInMemory) {

		List<Room> rooms = Lists.newArrayList();

		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT * FROM rooms WHERE enabled = 1 AND room_type = " + RoomType.PUBLIC.getTypeCode() + " ORDER BY order_id ASC", sqlConnection);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				int id = resultSet.getInt("id");

				Room room = Roseau.getGame().getRoomManager().getRoomById(id);

				if (room == null) {
					room = this.fill(resultSet);
					room.setOrderId(resultSet.getInt("order_id"));
				}

				rooms.add(room);

				if (storeInMemory) {
					Roseau.getGame().getRoomManager().add(room);
				}
			}

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return rooms;
	}

	@Override
	public List<Room> getPlayerRooms(PlayerDetails details, boolean storeInMemory) {

		List<Room> rooms = Lists.newArrayList();

		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT * FROM rooms WHERE owner_id = " + details.getId(), sqlConnection);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				int id = resultSet.getInt("id");

				Room room = Roseau.getGame().getRoomManager().getRoomById(id);

				if (room == null) {
					room = this.fill(resultSet);
				}

				rooms.add(room);

				if (storeInMemory) {
					Roseau.getGame().getRoomManager().add(room);
				}
			}

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return rooms;
	}

	@Override
	public Room getRoom(int roomId) {
		return getRoom(roomId, false);
	}

	@Override
	public Room getRoom(int roomId, boolean storeInMemory) {

		Room room = null;
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT * FROM rooms WHERE id = " + roomId, sqlConnection);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {

				int id = resultSet.getInt("id");

				room = Roseau.getGame().getRoomManager().getRoomById(id);

				if (room == null) {
					room = this.fill(resultSet);
				}

				if (storeInMemory) {
					Roseau.getGame().getRoomManager().add(room);
				}
			}

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return room;
	}


	@Override
	public List<Integer> getRoomRights(int roomId) {

		List<Integer> rooms = Lists.newArrayList();

		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT * FROM room_rights WHERE room_id = " + roomId, sqlConnection);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				rooms.add(resultSet.getInt("user_id"));
			}

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}


		return rooms;
	}

	@Override
	public void deleteRoom(Room room) {
		this.dao.getStorage().execute("DELETE FROM rooms WHERE id = " + room.getData().getId());
	}

	@Override
	public Room createRoom(Player player, String name, String description, String model, int state) {

		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();

			preparedStatement = dao.getStorage().prepare("INSERT INTO rooms (name, description, owner_id, model, state) VALUES (?, ?, ?, ?, ?)", sqlConnection);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, description);
			preparedStatement.setInt(3, player.getDetails().getId());
			preparedStatement.setString(4, model);
			preparedStatement.setInt(5, state);
			preparedStatement.executeUpdate();

			ResultSet row = preparedStatement.getGeneratedKeys();

			if (row != null && row.next()) {
				return this.getRoom(row.getInt(1), true);
			}

		} catch (SQLException e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return null;
	}
	
	@Override
	public void updateRoom(Room room) {

		RoomData data = room.getData();

		
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();

			preparedStatement = dao.getStorage().prepare("UPDATE rooms SET name = ?, description = ?, state = ?, password = ?, wallpaper = ?, floor = ? WHERE id = ?", sqlConnection);
			
			preparedStatement.setString(1, data.getName());
			preparedStatement.setString(2, data.getDescription());
			preparedStatement.setInt(3, data.getState().getStateCode());
			preparedStatement.setString(4, data.getPassword());
			preparedStatement.setString(5, data.getWall());
			preparedStatement.setString(6, data.getFloor());
			preparedStatement.setInt(7, data.getId());
			
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

	}
	
	@Override
	public RoomModel getModel(String model) {
		return roomModels.get(model);
	}

	@Override
	public Room fill(ResultSet row) throws SQLException {

		RoomType type = RoomType.getType(row.getInt("room_type"));

		PlayerDetails details = null;

		if (type == RoomType.PRIVATE) {
			details = Roseau.getDataAccess().getPlayer().getDetails(row.getInt("owner_id"));
		}

		Room instance = new Room();

		instance.getData().fill(row.getInt("id"), (row.getInt("hidden") == 1), type, details == null ? 0 : details.getId(), details == null ? "" : details.getUsername(), row.getString("name"), 
				row.getInt("state"), row.getString("password"), row.getInt("users_now"), row.getInt("users_max"), row.getString("description"), row.getString("model"),
				row.getString("cct"), row.getString("wallpaper"), row.getString("floor"));

		return instance;
	}



}
