package org.alexdev.roseau.dao.mysql;

import java.util.Map;

import org.alexdev.roseau.dao.CatalogueDao;
import org.alexdev.roseau.dao.mysql.entity.CatalogueDealEntity;
import org.alexdev.roseau.dao.mysql.entity.CatalogueEntity;
import org.alexdev.roseau.game.catalogue.CatalogueDeal;
import org.alexdev.roseau.game.catalogue.CatalogueItem;
import org.alexdev.roseau.log.Log;
import org.oldskooler.entity4j.DbContext;

import com.google.common.collect.Maps;

public class MySQLCatalogueDao implements CatalogueDao {

	private MySQLDao dao;

	public MySQLCatalogueDao(MySQLDao dao) {
		this.dao = dao;
	}

	@Override
	public Map<String, CatalogueItem> getBuyableItems() {
		Map<String, CatalogueItem> buyableItems = Maps.newHashMap();

		try (DbContext context = this.dao.getStorage().context()) {
			for (CatalogueEntity entity : context.from(CatalogueEntity.class).toList()) {
				buyableItems.put(entity.getCallId(), new CatalogueItem(entity.getCallId(), entity.getDefinitionId(), entity.getCredits()));
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return buyableItems;
	}

	@Override
	public Map<String, CatalogueDeal> getItemDeals() {
		Map<String, CatalogueDeal> deals = Maps.newHashMap();

		try (DbContext context = this.dao.getStorage().context()) {
			for (CatalogueDealEntity entity : context.from(CatalogueDealEntity.class).toList()) {
				deals.put(entity.getCallId(), new CatalogueDeal(entity.getCallId(), entity.getProducts().split(","), entity.getCost()));
			}
		} catch (Exception e) {
			Log.exception(e);
		}

		return deals;
	}
}
