package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.catalogue.CatalogueItem;
import org.alexdev.kepler.game.catalogue.CataloguePackage;
import org.alexdev.kepler.game.catalogue.CataloguePage;
import org.alexdev.kepler.game.player.PlayerRank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CatalogueDao {

    /**
     * Get the catalogue pages.
     *
     * @return the list of catalogue pages
     */
    public static List<CataloguePage> getPages() {
        List<CataloguePage> pages = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet row = null;

        try {
            conn = Storage.getStorage().getConnection();
            stmt = Storage.getStorage().prepare("SELECT * FROM catalogue_pages ORDER BY order_id ASC", conn);
            row = stmt.executeQuery();

            while (row.next()) {
                CataloguePage page = new CataloguePage(row.getInt("id"), PlayerRank.getRankForId(row.getInt("min_role")), row.getBoolean("index_visible"),
                        row.getBoolean("is_club_only"), row.getString("name_index"), row.getString("link_list"), row.getString("name"),
                        row.getString("layout"), row.getString("image_headline"), row.getString("image_teasers"), row.getString("body"),
                        row.getString("label_pick"), row.getString("label_extra_s"), row.getString("label_extra_t"));

                pages.add(page);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(row);
            Storage.closeSilently(stmt);
            Storage.closeSilently(conn);
        }

        return pages;
    }

    /**
     * Get the catalogue items.
     *
     * @return the list of catalogue items
     */
    public static List<CatalogueItem> getItems() {
        List<CatalogueItem> pages = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT \n" +
                    "cait.id, cait.page_id, cait.order_id, cait.price, cait.is_hidden, cait.amount, cait.definition_id,cait.item_specialspriteid,cait.is_package,cait.package_name,cait.package_description,cait.sale_code,\n" +
                    "IF(cait.is_package, cait.name, (SELECT name FROM items_definitions WHERE id = cait.definition_id)) AS name,IF(cait.is_package, cait.description, (SELECT description FROM items_definitions WHERE id = cait.definition_id)) AS description\n" +
                    "FROM catalogue_items AS cait", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                CatalogueItem item = new CatalogueItem(resultSet.getInt("id"), resultSet.getString("sale_code"), resultSet.getString("page_id"),
                        resultSet.getInt("order_id"),  resultSet.getInt("price"), resultSet.getBoolean("is_hidden"),
                        resultSet.getInt("definition_id"),  resultSet.getInt("item_specialspriteid"),
                        resultSet.getString("name"), resultSet.getString("description"),
                        resultSet.getBoolean("is_package"), resultSet.getString("package_name"),
                        resultSet.getString("package_description"));

                pages.add(item);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return pages;
    }

    /**
     * Get the catalogue packages.
     *
     * @return the list of catalogue packages
     */
    public static List<CataloguePackage> getPackages() {
        List<CataloguePackage> packages = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM catalogue_packages", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                CataloguePackage cataloguePackage = new CataloguePackage(resultSet.getString("salecode"), resultSet.getInt("definition_id"),
                        resultSet.getInt("special_sprite_id"), resultSet.getInt("amount"));

                packages.add(cataloguePackage);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return packages;
    }

    /**
     * Save catalogue item price.
     */
    public static void setPrice(String saleCode, int price) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE catalogue_items SET price = ? WHERE sale_code = ?", sqlConnection);
            preparedStatement.setInt(1, price);
            preparedStatement.setString(2, saleCode);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
