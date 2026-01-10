package org.alexdev.roseau.dao.jdbc;

import org.oldskooler.simplelogger4j.SimpleLog;
import org.alexdev.roseau.dao.CatalogueDao;
import org.alexdev.roseau.game.catalogue.CatalogueDeal;
import org.alexdev.roseau.game.catalogue.CatalogueItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class JdbcCatalogueDao implements CatalogueDao {

    private static final SimpleLog logger = SimpleLog.of(JdbcCatalogueDao.class);
    
	private final JdbcDao dao;

	public JdbcCatalogueDao(JdbcDao dao) {
		this.dao = dao;
	}

	@Override
	public Map<String, CatalogueItem> getBuyableItems() {
		String sql = "SELECT * FROM catalogue";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {
			
			Map<String, CatalogueItem> buyableItems = new HashMap<>();
			while (resultSet.next()) {
				buyableItems.put(
					resultSet.getString("call_id"),
					new CatalogueItem(
						resultSet.getString("call_id"),
						resultSet.getInt("definition_id"),
						resultSet.getInt("credits")
					)
				);
			}
			return buyableItems;
			
		} catch (SQLException e) {
			logger.error("Failed to get buyable items", e);
			return new HashMap<>();
		}
	}

	@Override
	public Map<String, CatalogueDeal> getItemDeals() {
		String sql = "SELECT * FROM catalogue_deals";
		
		try (Connection connection = dao.getStorage().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {
			
			Map<String, CatalogueDeal> deals = new HashMap<>();
			while (resultSet.next()) {
				String productsStr = resultSet.getString("products");
				String[] products = Optional.ofNullable(productsStr)
					.map(str -> str.split(","))
					.orElse(new String[0]);
				
				deals.put(
					resultSet.getString("call_id"),
					new CatalogueDeal(
						resultSet.getString("call_id"),
						products,
						resultSet.getInt("cost")
					)
				);
			}
			return deals;
			
		} catch (SQLException e) {
			logger.error("Failed to get item deals", e);
			return new HashMap<>();
		}
	}
}
