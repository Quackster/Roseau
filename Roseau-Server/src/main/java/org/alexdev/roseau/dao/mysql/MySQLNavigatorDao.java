package org.alexdev.roseau.dao.mysql;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.dao.NavigatorDao;
import org.alexdev.roseau.dao.mysql.entity.RoomEntity;
import org.alexdev.roseau.dao.mysql.mapper.EntityMapper;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.log.Log;
import org.oldskooler.entity4j.DbContext;

import com.google.common.collect.Lists;

public class MySQLNavigatorDao implements NavigatorDao {

	private MySQLDao dao;

	public MySQLNavigatorDao(MySQLDao dao) {
		this.dao = dao;
	}

	@Override
	public List<Room> getRoomsByLikeName(String name) {
		List<Room> rooms = Lists.newArrayList();

		try (DbContext context = this.dao.getStorage().context()) {
			for (RoomEntity entity : context.from(RoomEntity.class)
					.filter(f -> f.like(RoomEntity::getName, "%" + name + "%")
							.and()
							.equals(RoomEntity::getRoomType, RoomType.PRIVATE.getTypeCode()))
					.toList()) {
				Room room = Roseau.getGame().getRoomManager().getRoomByID(entity.getId());
				rooms.add(room == null ? EntityMapper.toRoom(entity) : room);
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return rooms;
	}
}
