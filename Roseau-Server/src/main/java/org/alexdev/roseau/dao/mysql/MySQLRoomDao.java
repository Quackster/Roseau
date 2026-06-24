package org.alexdev.roseau.dao.mysql;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.dao.RoomDao;
import org.alexdev.roseau.dao.mysql.entity.RoomBotEntity;
import org.alexdev.roseau.dao.mysql.entity.RoomChatlogEntity;
import org.alexdev.roseau.dao.mysql.entity.RoomEntity;
import org.alexdev.roseau.dao.mysql.entity.RoomModelEntity;
import org.alexdev.roseau.dao.mysql.entity.RoomPublicConnectionEntity;
import org.alexdev.roseau.dao.mysql.entity.RoomRightEntity;
import org.alexdev.roseau.dao.mysql.mapper.EntityMapper;
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
import org.alexdev.roseau.log.Log;
import org.oldskooler.entity4j.DbContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MySQLRoomDao implements RoomDao {

	private MySQLDao dao;
	private Map<String, RoomModel> roomModels;

	public MySQLRoomDao(MySQLDao dao) {
		this.dao = dao;
		this.roomModels = Maps.newHashMap();
		this.loadRoomModels();
	}

	@Override
	public List<Room> getPublicRooms(boolean storeInMemory) {
		List<Room> rooms = Lists.newArrayList();

		try (DbContext context = this.dao.getStorage().context()) {
			for (RoomEntity entity : context.from(RoomEntity.class)
					.filter(f -> f.equals(RoomEntity::getEnabled, 1)
							.and()
							.equals(RoomEntity::getRoomType, RoomType.PUBLIC.getTypeCode()))
					.orderBy(o -> o.col(RoomEntity::getOrderId).asc())
					.toList()) {
				Room room = getCachedOrMapped(entity);
				room.setOrderID(entity.getOrderId());
				rooms.add(room);

				if (storeInMemory) {
					Roseau.getGame().getRoomManager().add(room);
				}
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return rooms;
	}

	@Override
	public List<Integer> getPublicRoomIDs() {
		List<Integer> rooms = Lists.newArrayList();

		try (DbContext context = this.dao.getStorage().context()) {
			for (RoomEntity entity : context.from(RoomEntity.class)
					.filter(f -> f.equals(RoomEntity::getEnabled, 1)
							.and()
							.equals(RoomEntity::getRoomType, RoomType.PUBLIC.getTypeCode())
							.and()
							.equals(RoomEntity::getHidden, 0))
					.orderBy(o -> o.col(RoomEntity::getOrderId).asc())
					.toList()) {
				rooms.add(entity.getId());
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return rooms;
	}

	@Override
	public List<Integer> setRoomConnections(Room room) {
		List<Integer> connections = Lists.newArrayList();

		try (DbContext context = this.dao.getStorage().context()) {
			for (RoomPublicConnectionEntity entity : context.from(RoomPublicConnectionEntity.class)
					.filter(f -> f.equals(RoomPublicConnectionEntity::getRoomId, room.getData().getID()))
					.toList()) {
				int toRoomID = entity.getToId();

				if (!connections.contains(toRoomID)) {
					connections.add(toRoomID);
				}

				for (String coordinate : entity.getCoordinates().split(" ")) {
					Position pos = new Position(coordinate);
					Position doorPosition = null;

					if (entity.getDoorX() > -1) {
						doorPosition = new Position(entity.getDoorX(), entity.getDoorY(), entity.getDoorZ());
					}

					room.getMapping().getConnections()[pos.getX()][pos.getY()] = new RoomConnection(room.getData().getID(), toRoomID, doorPosition);
				}
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return connections;
	}

	@Override
	public List<Room> getPlayerRooms(PlayerDetails details, boolean storeInMemory) {
		List<Room> rooms = Lists.newArrayList();

		try (DbContext context = this.dao.getStorage().context()) {
			for (RoomEntity entity : context.from(RoomEntity.class)
					.filter(f -> f.equals(RoomEntity::getOwnerId, details.getID()))
					.toList()) {
				Room room = getCachedOrMapped(entity);
				rooms.add(room);

				if (storeInMemory) {
					Roseau.getGame().getRoomManager().add(room);
				}
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return rooms;
	}

	@Override
	public List<Room> getLatestPlayerRooms(List<Integer> blacklist, int range) {
		List<Room> rooms = Lists.newArrayList();

		try (DbContext context = this.dao.getStorage().context()) {
			for (RoomEntity entity : context.from(RoomEntity.class)
					.filter(f -> f.equals(RoomEntity::getRoomType, RoomType.PRIVATE.getTypeCode()))
					.orderBy(o -> o.col(RoomEntity::getId).desc())
					.offset(range * 11)
					.limit(11)
					.toList()) {
				if (blacklist.contains(entity.getId())) {
					continue;
				}

				rooms.add(getCachedOrMapped(entity));
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return rooms;
	}

	@Override
	public Room getRoom(int roomID, boolean storeInMemory) {
		try (DbContext context = this.dao.getStorage().context()) {
			RoomEntity entity = context.from(RoomEntity.class)
					.filter(f -> f.equals(RoomEntity::getId, roomID))
					.first()
					.orElse(null);

			if (entity == null) {
				return null;
			}

			Room room = getCachedOrMapped(entity);

			if (storeInMemory) {
				Roseau.getGame().getRoomManager().add(room);
			}

			return room;
		} catch (Exception e) {
			Log.exception(e);
			return null;
		}
	}

	@Override
	public List<Integer> getRoomRights(int roomID) {
		List<Integer> rooms = Lists.newArrayList();

		try (DbContext context = this.dao.getStorage().context()) {
			for (RoomRightEntity entity : context.from(RoomRightEntity.class)
					.filter(f -> f.equals(RoomRightEntity::getRoomId, roomID))
					.toList()) {
				rooms.add(entity.getUserId());
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return rooms;
	}

	@Override
	public void saveRoomRights(int roomID, List<Integer> rights) {
		try (DbContext context = this.dao.getStorage().context()) {
			context.from(RoomRightEntity.class)
					.filter(f -> f.equals(RoomRightEntity::getRoomId, roomID))
					.delete();

			for (int userID : rights) {
				RoomRightEntity entity = new RoomRightEntity();
				entity.setRoomId(roomID);
				entity.setUserId(userID);
				context.insert(entity);
			}
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	@Override
	public void deleteRoom(Room room) {
		try (DbContext context = this.dao.getStorage().context()) {
			context.from(RoomEntity.class)
					.filter(f -> f.equals(RoomEntity::getId, room.getData().getID()))
					.delete();
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	@Override
	public Room createRoom(Player player, String name, String description, String model, int state, boolean showOwnerName) {
		RoomEntity entity = new RoomEntity();
		entity.setName(name);
		entity.setDescription(description);
		entity.setOwnerId(player.getDetails().getID());
		entity.setModel(model);
		entity.setState(state);
		entity.setShowOwnerName(showOwnerName ? 1 : 0);
		entity.setRoomType(RoomType.PRIVATE.getTypeCode());

		try (DbContext context = this.dao.getStorage().context()) {
			context.insert(entity);
			return this.getRoom(entity.getId(), true);
		} catch (Exception e) {
			Log.exception(e);
			return null;
		}
	}

	@Override
	public Room saveChatlog(Player chatter, int roomID, String chatType, String message) {
		RoomChatlogEntity entity = new RoomChatlogEntity();
		entity.setUser(chatter.getDetails().getName());
		entity.setRoomId(roomID);
		entity.setTimestamp(DateTime.getTime());
		entity.setMessageType(chatTypeCode(chatType));
		entity.setMessage(message);

		try (DbContext context = this.dao.getStorage().context()) {
			context.insert(entity);
		} catch (Exception e) {
			Log.exception(e);
		}

		return null;
	}

	@Override
	public void updateRoom(Room room) {
		RoomData data = room.getData();

		try (DbContext context = this.dao.getStorage().context()) {
			RoomEntity entity = context.from(RoomEntity.class)
					.filter(f -> f.equals(RoomEntity::getId, data.getID()))
					.first()
					.orElse(null);

			if (entity == null) {
				return;
			}

			entity.setName(data.getName());
			entity.setDescription(data.getDescription());
			entity.setState(data.getState().getStateCode());
			entity.setPassword(data.getPassword());
			entity.setWallpaper(data.getWall());
			entity.setFloor(data.getFloor());
			entity.setAllsuperuser(data.hasAllSuperUser() ? 1 : 0);
			entity.setShowOwnerName(data.showOwnerName() ? 1 : 0);
			context.update(entity);
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	@Override
	public List<Bot> getBots(Room room, int roomID) {
		List<Bot> bots = Lists.newArrayList();

		try (DbContext context = this.dao.getStorage().context()) {
			for (RoomBotEntity entity : context.from(RoomBotEntity.class)
					.filter(f -> f.equals(RoomBotEntity::getRoomId, roomID))
					.toList()) {
				Bot bot = toBot(room, entity);
				bots.add(bot);
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return bots;
	}

	@Override
	public RoomModel getModel(String model) {
		return roomModels.get(model);
	}

	private void loadRoomModels() {
		try (DbContext context = this.dao.getStorage().context()) {
			for (RoomModelEntity entity : context.from(RoomModelEntity.class).toList()) {
				roomModels.put(entity.getId(), EntityMapper.toRoomModel(entity));
			}
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	private Room getCachedOrMapped(RoomEntity entity) {
		Room room = Roseau.getGame().getRoomManager().getRoomByID(entity.getId());
		return room == null ? EntityMapper.toRoom(entity) : room;
	}

	private Bot toBot(Room room, RoomBotEntity entity) {
		List<int[]> positions = Lists.newArrayList();
		List<String> responses = Lists.newArrayList();
		List<String> triggers = Lists.newArrayList();

		if (entity.getWalkTo() != null && entity.getWalkTo().length() > 0) {
			for (String coordinate : entity.getWalkTo().split(" ")) {
				int x = Integer.valueOf(coordinate.split(",")[0]);
				int y = Integer.valueOf(coordinate.split(",")[1]);
				positions.add(new int[] { x, y });
			}
		}

		String dbResponses = entity.getResponses();

		if (dbResponses != null && dbResponses.contains("|")) {
			responses.addAll(Arrays.asList(dbResponses.split("\\|")));
		} else if (dbResponses != null) {
			responses.add(dbResponses);
		}

		String dbTriggers = entity.getTriggers();

		if (dbTriggers != null && dbTriggers.contains(",")) {
			triggers.addAll(Arrays.asList(dbTriggers.split(",")));
		} else if (dbTriggers != null) {
			triggers.add(dbTriggers);
		}

		Bot bot = new Bot(new Position(entity.getStartX(), entity.getStartY(), entity.getStartZ(), entity.getStartRotation()),
				positions, responses, triggers);
		bot.getDetails().fill(entity.getId(), entity.getName(), entity.getMotto(), entity.getFigure(), "Male");
		bot.getRoomUser().getPosition().setX(bot.getStartPosition().getX());
		bot.getRoomUser().getPosition().setY(bot.getStartPosition().getY());
		bot.getRoomUser().getPosition().setZ(bot.getStartPosition().getZ());
		bot.getRoomUser().getPosition().setHeadRotation(bot.getStartPosition().getRotation());
		bot.getRoomUser().getPosition().setBodyRotation(bot.getStartPosition().getRotation());
		bot.getRoomUser().setRoom(room);
		return bot;
	}

	private int chatTypeCode(String chatType) {
		switch (chatType) {
			case "CHAT":
				return 0;
			case "SHOUT":
				return 1;
			default:
				return 2;
		}
	}
}
