package org.alexdev.roseau.dao.jdbc;

import org.oldskooler.simplelogger4j.SimpleLog;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.dao.RoomDao;
import org.alexdev.roseau.dao.util.IProcessStorage;
import org.alexdev.roseau.game.player.Bot;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.RoomConnection;
import org.alexdev.roseau.game.room.RoomData;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.game.room.model.RoomModel;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.log.DateTime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JdbcRoomDao extends IProcessStorage<Room, ResultSet> implements RoomDao {

    private static final SimpleLog logger = SimpleLog.of(JdbcRoomDao.class);
    
	private final JdbcDao dao;
	private final Map<String, RoomModel> roomModels;

	public JdbcRoomDao(JdbcDao dao) {
		this.dao = dao;
		this.roomModels = new HashMap<>();
		loadRoomModels();
	}

	private void loadRoomModels() {
		String sql = "SELECT * FROM room_models";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {
			
			while (resultSet.next()) {
				RoomModel model = new RoomModel(
					resultSet.getString("id"),
					resultSet.getString("heightmap"),
					resultSet.getInt("door_x"),
					resultSet.getInt("door_y"),
					resultSet.getInt("door_z"),
					resultSet.getInt("door_dir"),
					resultSet.getByte("has_pool") == 1,
					resultSet.getByte("disable_height_check") == 1
				);
				roomModels.put(resultSet.getString("id"), model);
			}
			
		} catch (SQLException e) {
			logger.error("Failed to load room models", e);
		}
	}

	@Override
	public List<Room> getPublicRooms(boolean storeInMemory) {
		String sql = "SELECT * FROM rooms WHERE enabled = 1 AND room_type = ? ORDER BY order_id ASC";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, RoomType.PUBLIC.getTypeCode());
			
			try (ResultSet resultSet = statement.executeQuery()) {
				List<Room> rooms = new ArrayList<>();
				while (resultSet.next()) {
					int id = resultSet.getInt("id");
					
					Room room = Optional.ofNullable(Roseau.getGame().getRoomManager().getRoomByID(id))
						.orElseGet(() -> {
							try {
								Room newRoom = fill(resultSet);
								newRoom.setOrderId(resultSet.getInt("order_id"));
								return newRoom;
							} catch (Exception e) {
								logger.error("Error filling room", e);
								return null;
							}
						});
					
					Optional.ofNullable(room).ifPresent(r -> {
						rooms.add(r);
						if (storeInMemory) {
							Roseau.getGame().getRoomManager().add(r);
						}
					});
				}
				return rooms;
			}
			
		} catch (Exception e) {
			logger.error("Failed to get public rooms", e);
			return new ArrayList<>();
		}
	}

	@Override
	public List<Integer> getPublicRoomIDs() {
		String sql = "SELECT id FROM rooms WHERE enabled = 1 AND room_type = ? AND hidden = 0 ORDER BY order_id ASC";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {
			
			List<Integer> rooms = new ArrayList<>();
			while (resultSet.next()) {
				rooms.add(resultSet.getInt("id"));
			}
			return rooms;
			
		} catch (SQLException e) {
			logger.error("Failed to get public room IDs", e);
			return new ArrayList<>();
		}
	}

	@Override
	public List<Integer> setRoomConnections(Room room) {
		String sql = "SELECT * FROM room_public_connections WHERE room_id = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, room.getData().getId());
			
			try (ResultSet resultSet = statement.executeQuery()) {
				Set<Integer> connectionSet = new HashSet<>();
				
				while (resultSet.next()) {
					int toRoomId = resultSet.getInt("to_id");
					connectionSet.add(toRoomId);
					
					Optional.ofNullable(resultSet.getString("coordinates"))
						.filter(coords -> !coords.isEmpty())
						.ifPresent(coordinates -> {
							Stream.of(coordinates.split(" "))
								.map(Position::new)
								.forEach(pos -> {
                                    Position doorPosition = null;
                                    try {
                                        doorPosition = Optional.of(resultSet.getInt("door_x"))
                                            .filter(doorX -> doorX > -1)
                                            .map(doorX -> {
                                                try {
                                                    return new Position(
                                                        resultSet.getInt("door_x"),
                                                        resultSet.getInt("door_y"),
                                                        resultSet.getInt("door_z")
                                                    );
                                                } catch (SQLException e) {
                                                    logger.error("Failed to get coords " + room.getData().getId(), e);
                                                }

                                                return null;
                                            })
                                            .orElse(null);
                                    } catch (SQLException e) {
                                        logger.error("Failed to get coords " + room.getData().getId(), e);
                                    }

                                    room.getMapping().getConnections()[pos.getX()][pos.getY()] =
										new RoomConnection(room.getData().getId(), toRoomId, doorPosition);
								});
						});
				}
				
				return new ArrayList<>(connectionSet);
			}
			
		} catch (SQLException e) {
			logger.error("Failed to set room connections for room: " + room.getData().getId(), e);
			return new ArrayList<>();
		}
	}

	@Override
	public List<Room> getPlayerRooms(PlayerDetails details, boolean storeInMemory) {
		String sql = "SELECT * FROM rooms WHERE owner_id = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, details.getId());
			
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
					
					Optional.ofNullable(room).ifPresent(r -> {
						rooms.add(r);
						if (storeInMemory) {
							Roseau.getGame().getRoomManager().add(r);
						}
					});
				}
				return rooms;
			}
			
		} catch (SQLException e) {
			logger.error("Failed to get player rooms for user: " + details.getId(), e);
			return new ArrayList<>();
		}
	}

	@Override
	public List<Room> getLatestPlayerRooms(List<Integer> blacklist, int range) {
		int offset = range * 11;
		int limit = 11;
		String sql = "SELECT * FROM rooms WHERE room_type = 0 ORDER BY id DESC LIMIT ? OFFSET ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, limit);
			statement.setInt(2, offset);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				Set<Integer> blacklistSet = new HashSet<>(blacklist);
				
				return Stream.generate(() -> {
					try {
						if (resultSet.next()) {
							return resultSet;
						}
						return null;
					} catch (SQLException e) {
						logger.error("Error reading result set", e);
						return null;
					}
				})
				.takeWhile(rs -> rs != null)
				.map(rs -> {
					try {
						int id = rs.getInt("id");
						if (blacklistSet.contains(id)) {
							return null;
						}
						return Optional.ofNullable(Roseau.getGame().getRoomManager().getRoomByID(id))
							.orElseGet(() -> {
								try {
									return fill(rs);
								} catch (Exception e) {
									logger.error("Error filling room", e);
									return null;
								}
							});
					} catch (SQLException e) {
						logger.error("Error processing room", e);
						return null;
					}
				})
				.filter(room -> room != null)
				.collect(Collectors.toList());
			}
			
		} catch (SQLException e) {
			logger.error("Failed to get latest player rooms", e);
			return new ArrayList<>();
		}
	}

	@Override
	public Room getRoom(int roomId, boolean storeInMemory) {
		String sql = "SELECT * FROM rooms WHERE id = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, roomId);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				return Optional.ofNullable(resultSet.next() ? resultSet : null)
					.map(rs -> {
						try {
							int id = rs.getInt("id");
							Room room = Optional.ofNullable(Roseau.getGame().getRoomManager().getRoomByID(id))
								.orElseGet(() -> {
									try {
										return fill(rs);
									} catch (Exception e) {
										logger.error("Error filling room", e);
										return null;
									}
								});
							
							Optional.ofNullable(room).filter(r -> storeInMemory)
								.ifPresent(Roseau.getGame().getRoomManager()::add);
							
							return room;
						} catch (SQLException e) {
							logger.error("Error processing room", e);
							return null;
						}
					})
					.orElse(null);
			}
			
		} catch (SQLException e) {
			logger.error("Failed to get room: " + roomId, e);
			return null;
		}
	}

	@Override
	public List<Integer> getRoomRights(int roomId) {
		String sql = "SELECT user_id FROM room_rights WHERE room_id = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {
			
			List<Integer> rights = new ArrayList<>();
			while (resultSet.next()) {
				rights.add(resultSet.getInt("user_id"));
			}
			return rights;
			
		} catch (SQLException e) {
			logger.error("Failed to get room rights for room: " + roomId, e);
			return new ArrayList<>();
		}
	}

	@Override
	public void saveRoomRights(int roomId, List<Integer> rights) {
		String deleteSql = "DELETE FROM room_rights WHERE room_id = ?";
		String insertSql = "INSERT INTO room_rights (room_id, user_id) VALUES (?, ?)";
		
		try (Connection connection = dao.getStorage().getConnection()) {
			// Delete existing rights
			try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
				deleteStatement.setInt(1, roomId);
				deleteStatement.executeUpdate();
			}
			
			// Insert new rights using batch update
			try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
				rights.forEach(userId -> {
					try {
						insertStatement.setInt(1, roomId);
						insertStatement.setInt(2, userId);
						insertStatement.addBatch();
					} catch (SQLException e) {
						logger.error("Error adding batch for user ID: " + userId, e);
					}
				});
				insertStatement.executeBatch();
			}
			
		} catch (SQLException e) {
			logger.error("Failed to save room rights for room: " + roomId, e);
		}
	}

	@Override
	public void deleteRoom(Room room) {
		String sql = "DELETE FROM rooms WHERE id = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, room.getData().getId());
			statement.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Failed to delete room: " + room.getData().getId(), e);
		}
	}

	@Override
	public Room createRoom(Player player, String name, String description, String model, int state, boolean showOwnerName) {
		String sql = """
			INSERT INTO rooms (name, description, owner_id, model, state, show_owner_name)
			VALUES (?, ?, ?, ?, ?, ?)
			""";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
			statement.setString(1, name);
			statement.setString(2, description);
			statement.setInt(3, player.getDetails().getId());
			statement.setString(4, model);
			statement.setInt(5, state);
			statement.setInt(6, showOwnerName ? 1 : 0);
			statement.executeUpdate();
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				return Optional.ofNullable(generatedKeys.next() ? generatedKeys.getInt(1) : null)
					.map(roomId -> getRoom(roomId, true))
					.orElse(null);
			}
			
		} catch (SQLException e) {
			logger.error("Failed to create room for player: " + player.getDetails().getId(), e);
			return null;
		}
	}

	@Override
	public Room saveChatlog(Player chatter, int roomId, String chatType, String message) {
		String sql = """
			INSERT INTO room_chatlogs (user, room_id, timestamp, message_type, message)
			VALUES (?, ?, ?, ?, ?)
			""";
		
		int messageType = switch (chatType) {
			case "CHAT" -> 0;
			case "SHOUT" -> 1;
			default -> 2;
		};
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, chatter.getDetails().getName());
			statement.setInt(2, roomId);
			statement.setLong(3, DateTime.getTime());
			statement.setInt(4, messageType);
			statement.setString(5, message);
			
			statement.execute();
			
		} catch (SQLException e) {
			logger.error("Failed to save chatlog for room: " + roomId, e);
		}
		
		return null;
	}

	@Override
	public void updateRoom(Room room) {
		RoomData data = room.getData();
		String sql = """
			UPDATE rooms
			SET name = ?, description = ?, state = ?, password = ?, wallpaper = ?, floor = ?, allsuperuser = ?, show_owner_name = ?
			WHERE id = ?
			""";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, data.getName());
			statement.setString(2, data.getDescription());
			statement.setInt(3, data.getState().getStateCode());
			statement.setString(4, data.getPassword());
			statement.setString(5, data.getWall());
			statement.setString(6, data.getFloor());
			statement.setInt(7, data.hasAllSuperUser() ? 1 : 0);
			statement.setInt(8, data.showOwnerName() ? 1 : 0);
			statement.setInt(9, data.getId());
			
			statement.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Failed to update room: " + data.getId(), e);
		}
	}

	@Override
	public List<Bot> getBots(Room room, int roomId) {
		String sql = "SELECT id, name, figure, motto, start_x, start_y, start_z, start_rotation, walk_to, messages, triggers, responses FROM room_bots WHERE room_id = ?";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, roomId);
			
			try (ResultSet rs = statement.executeQuery()) {
				List<Bot> bots = new ArrayList<>();
				while (rs.next()) {
					List<int[]> positions = Optional.ofNullable(rs.getString("walk_to"))
						.filter(walkTo -> !walkTo.isEmpty())
						.map(walkTo -> Stream.of(walkTo.split(" "))
							.map(coordinate -> {
								String[] parts = coordinate.split(",");
								if (parts.length >= 2) {
									int x = Integer.parseInt(parts[0]);
									int y = Integer.parseInt(parts[1]);
									return new int[]{x, y};
								}
								return null;
							})
							.filter(pos -> pos != null)
							.collect(Collectors.toList()))
						.orElse(new ArrayList<>());
					
					List<String> responses = Optional.ofNullable(rs.getString("responses"))
						.map(dbResponses -> dbResponses.contains("|") 
							? List.of(dbResponses.split("\\|"))
							: List.of(dbResponses))
						.orElse(new ArrayList<>());
					
					List<String> triggers = Optional.ofNullable(rs.getString("triggers"))
						.map(dbTriggers -> dbTriggers.contains(",")
							? List.of(dbTriggers.split(","))
							: List.of(dbTriggers))
						.orElse(new ArrayList<>());
					
					Bot bot = new Bot(
						new Position(
							rs.getInt("start_x"),
							rs.getInt("start_y"),
							rs.getInt("start_z"),
							rs.getInt("start_rotation")
						),
						positions,
						responses,
						triggers
					);
					
					bot.getDetails().fill(
						rs.getInt("id"),
						rs.getString("name"),
						rs.getString("motto"),
						rs.getString("figure"),
						"Male"
					);
					
					bot.getRoomUser().getPosition().setX(bot.getStartPosition().getX());
					bot.getRoomUser().getPosition().setY(bot.getStartPosition().getY());
					bot.getRoomUser().getPosition().setZ(bot.getStartPosition().getZ());
					bot.getRoomUser().getPosition().setHeadRotation(bot.getStartPosition().getRotation());
					bot.getRoomUser().getPosition().setBodyRotation(bot.getStartPosition().getRotation());
					bot.getRoomUser().setRoom(room);
					
					bots.add(bot);
				}
				return bots;
			}
			
		} catch (SQLException e) {
			logger.error("Failed to get bots for room: " + roomId, e);
			return new ArrayList<>();
		}
	}

	@Override
	public RoomModel getModel(String model) {
		return roomModels.get(model);
	}

	@Override
	public Room fill(ResultSet row) throws Exception {
		RoomType type = RoomType.getType(row.getInt("room_type"));
		
		PlayerDetails details = Optional.of(type)
			.filter(t -> t == RoomType.PRIVATE)
			.map(t -> {
                try {
                    return Roseau.getGame().getPlayerManager().getPlayerData(row.getInt("owner_id"));
                } catch (SQLException e) {
                    logger.error("Failed to get user data for owner id", e);
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
			row.getInt("allsuperuser") == 1,
			row.getInt("show_owner_name") == 1
		);
		
		Optional.ofNullable(details)
			.ifPresent(d -> instance.getData().setOwnerName(d.getName()));
		
		instance.load();
		
		return instance;
	}
}
