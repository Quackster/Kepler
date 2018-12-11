package org.alexdev.roseau.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.alexdev.roseau.dao.CatalogueDao;
import org.alexdev.roseau.game.catalogue.CatalogueDeal;
import org.alexdev.roseau.game.catalogue.CatalogueItem;
import org.alexdev.roseau.log.Log;

import com.google.common.collect.Maps;

public class MySQLCatalogueDao implements CatalogueDao {

	private MySQLDao dao;
	
	public MySQLCatalogueDao(MySQLDao dao) {
		this.dao = dao;
	}

	@Override
	public Map<String, CatalogueItem> getBuyableItems() {
		Map<String, CatalogueItem> buyableItems = Maps.newHashMap();
		
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT * FROM catalogue", sqlConnection);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				buyableItems.put(resultSet.getString("call_id"), new CatalogueItem(resultSet.getString("call_id"), resultSet.getInt("definition_id"), resultSet.getInt("credits")));
			}

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}
		
		return buyableItems;
	}
	
	@Override
	public Map<String, CatalogueDeal> getItemDeals() {
		Map<String, CatalogueDeal> deals = Maps.newHashMap();
		
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT * FROM catalogue_deals", sqlConnection);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				deals.put(resultSet.getString("call_id"), new CatalogueDeal(resultSet.getString("call_id"), resultSet.getString("products").split(","), resultSet.getInt("cost")));
			}

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}
		
		return deals;
	}


}
