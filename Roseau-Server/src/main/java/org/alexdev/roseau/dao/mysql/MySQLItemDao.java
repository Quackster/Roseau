package org.alexdev.roseau.dao.mysql;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.alexdev.roseau.dao.ItemDao;
import org.alexdev.roseau.dao.mysql.entity.ItemDefinitionEntity;
import org.alexdev.roseau.dao.mysql.entity.ItemEntity;
import org.alexdev.roseau.dao.mysql.entity.RoomPublicItemEntity;
import org.alexdev.roseau.dao.mysql.mapper.EntityMapper;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.ItemDefinition;
import org.alexdev.roseau.log.Log;
import org.oldskooler.entity4j.DbContext;

public class MySQLItemDao implements ItemDao {

	private MySQLDao dao;

	public MySQLItemDao(MySQLDao dao) {
		this.dao = dao;
	}

	@Override
	public Map<Integer, ItemDefinition> getDefinitions() {
		Map<Integer, ItemDefinition> definitions = new HashMap<Integer, ItemDefinition>();

		try (DbContext context = this.dao.getStorage().context()) {
			for (ItemDefinitionEntity entity : context.from(ItemDefinitionEntity.class).toList()) {
				definitions.put(entity.getId(), EntityMapper.toItemDefinition(entity));
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return definitions;
	}

	@Override
	public ConcurrentHashMap<Integer, Item> getPublicRoomItems(String model, int roomID) {
		ConcurrentHashMap<Integer, Item> items = new ConcurrentHashMap<Integer, Item>();

		try (DbContext context = this.dao.getStorage().context()) {
			for (RoomPublicItemEntity entity : context.from(RoomPublicItemEntity.class)
					.filter(f -> f.equals(RoomPublicItemEntity::getModel, model))
					.toList()) {
				items.put(entity.getId(), EntityMapper.toPublicItem(entity, roomID));
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return items;
	}

	@Override
	public ConcurrentHashMap<Integer, Item> getRoomItems(int roomID) {
		ConcurrentHashMap<Integer, Item> items = new ConcurrentHashMap<Integer, Item>();

		try (DbContext context = this.dao.getStorage().context()) {
			for (ItemEntity entity : context.from(ItemEntity.class)
					.filter(f -> f.equals(ItemEntity::getRoomId, roomID))
					.toList()) {
				items.put(entity.getId(), EntityMapper.toItem(entity));
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return items;
	}

	@Override
	public Item getItem(int itemID) {
		try (DbContext context = this.dao.getStorage().context()) {
			return context.from(ItemEntity.class)
					.filter(f -> f.equals(ItemEntity::getId, itemID))
					.first()
					.map(EntityMapper::toItem)
					.orElse(null);
		} catch (Exception e) {
			Log.exception(e);
			return null;
		}
	}

	@Override
	public void saveItem(Item item) {
		try (DbContext context = this.dao.getStorage().context()) {
			ItemEntity entity = context.from(ItemEntity.class)
					.filter(f -> f.equals(ItemEntity::getId, item.getID()))
					.first()
					.orElse(null);

			if (entity == null) {
				return;
			}

			entity.setExtraData(item.getDefinition().getBehaviour().isTeleporter() ? teleporterData(item) : item.getCustomData());

			if (item.getDefinition().getBehaviour().isOnWall()) {
				entity.setX(item.getWallPosition());
			} else {
				entity.setX(String.valueOf(item.getPosition().getX()));
			}

			entity.setY(item.getPosition().getY());
			entity.setZ(item.getPosition().getZ());
			entity.setRotation(item.getPosition().getRotation());
			entity.setRoomId(item.getRoomID());
			entity.setUserId(item.getOwnerID());
			context.update(entity);
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	@Override
	public void deleteItem(long id) {
		try (DbContext context = this.dao.getStorage().context()) {
			context.from(ItemEntity.class)
					.filter(f -> f.equals(ItemEntity::getId, (int) id))
					.delete();
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	private String teleporterData(Item item) {
		try {
			Integer.valueOf(item.getCustomData());
			return item.getCustomData();
		} catch (Exception e) {
			return String.valueOf(item.getTargetTeleporterID());
		}
	}
}
