package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.purse.Voucher;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.item.base.ItemDefinition;
import org.alexdev.kepler.messages.incoming.catalogue.GRPC;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class PurseDao {

    /**
     * Redeems a voucher.
     * Gets the voucher and then deletes it from the voucher table.
     *
     * @param voucherCode the string voucher code to redeem
     * @return the amount of credits redeemed or -1 if no voucher was found.
     */
    public static Voucher redeemVoucher(String voucherCode) {
        Voucher voucher = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ResultSet resultSet2 = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            
            //Get the voucher
            preparedStatement = Storage.getStorage().prepare("SELECT id,credits FROM vouchers WHERE voucher_code = ?", sqlConnection);
            preparedStatement.setString(1, voucherCode);
            resultSet = preparedStatement.executeQuery();
                 
            List<ItemDefinition> items = new ArrayList<>();
            
            int voucherID;
            int voucherCredits;
            
            //No results, quit
            if (!resultSet.next()) {
                return null;
            }
            
            voucherCredits = resultSet.getInt("credits");
            voucherID = resultSet.getInt("id");

            //Get related voucher items
            preparedStatement = Storage.getStorage().prepare("SELECT item_definition_id FROM vouchers_items WHERE voucher_id = ?", sqlConnection);
            preparedStatement.setInt(1, voucherID);
            resultSet2 = preparedStatement.executeQuery();
            
            //Find all items
            while(resultSet2.next())
            {
                int itemDefID = resultSet2.getInt("item_definition_id");
                ItemDefinition itemDef = ItemManager.getInstance().getDefinition(itemDefID);
                items.add(itemDef);
            }
            
            //Delete the voucher and related items
            preparedStatement = Storage.getStorage().prepare("DELETE FROM vouchers WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, voucherID);
            preparedStatement.executeQuery();
            
            preparedStatement = Storage.getStorage().prepare("DELETE FROM vouchers_items WHERE voucher_id = ?", sqlConnection);
            preparedStatement.setInt(1, voucherID);
            preparedStatement.executeQuery();  
            
            voucher = new Voucher(items, voucherCredits);
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            
            if(resultSet2 != null)
                Storage.closeSilently(resultSet2);
            
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        //No voucher found. Or error
        return voucher;
    }
}
