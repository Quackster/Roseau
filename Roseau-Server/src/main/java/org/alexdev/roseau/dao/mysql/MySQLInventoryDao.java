package org.alexdev.roseau.dao.mysql;

import java.util.List;

import org.alexdev.roseau.dao.InventoryDao;
import org.alexdev.roseau.dao.mysql.entity.ItemEntity;
import org.alexdev.roseau.dao.mysql.mapper.EntityMapper;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.log.Log;
import org.oldskooler.entity4j.DbContext;

import com.google.common.collect.Lists;

public class MySQLInventoryDao implements InventoryDao {

	private MySQLDao dao;

	public MySQLInventoryDao(MySQLDao dao) {
		this.dao = dao;
	}

	@Override
	public List<Item> getInventoryItems(int userID) {
		List<Item> items = Lists.newArrayList();

		try (DbContext context = this.dao.getStorage().context()) {
			for (ItemEntity entity : context.from(ItemEntity.class)
					.filter(f -> f.equals(ItemEntity::getRoomId, 0).and().equals(ItemEntity::getUserId, userID))
					.toList()) {
				items.add(EntityMapper.toItem(entity));
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return items;
	}

	@Override
	public Item getItem(long id) {
		try (DbContext context = this.dao.getStorage().context()) {
			return context.from(ItemEntity.class)
					.filter(f -> f.equals(ItemEntity::getId, (int) id))
					.first()
					.map(EntityMapper::toItem)
					.orElse(null);
		} catch (Exception e) {
			Log.exception(e);
			return null;
		}
	}

	@Override
	public Item newItem(int itemID, int ownerID, String extraData) {
		ItemEntity entity = new ItemEntity();
		entity.setUserId(ownerID);
		entity.setItemId(itemID);
		entity.setRoomId(0);
		entity.setX("0");
		entity.setExtraData(extraData);

		try (DbContext context = this.dao.getStorage().context()) {
			context.insert(entity);
			return EntityMapper.toItem(entity);
		} catch (Exception e) {
			Log.exception(e);
			return null;
		}
	}
}
