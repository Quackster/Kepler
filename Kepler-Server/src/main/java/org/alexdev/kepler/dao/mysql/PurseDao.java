package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PurseDao {

    /**
     * Redeems a voucher.
     * Gets the voucher and then deletes it from the voucher table.
     *
     * @param voucherCode the string voucher code to redeem
     * @return the amount of credits redeemed or -1 if no voucher was found.
     */
    public static int redeemVoucher(String voucherCode) {

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            //Get the voucher
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT Credits FROM vouchers WHERE VoucherCode = ?", sqlConnection);
            preparedStatement.setString(1, voucherCode);
            resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                //Delete the voucher
                preparedStatement = Storage.getStorage().prepare("DELETE FROM vouchers WHERE VoucherCode = ?", sqlConnection);
                preparedStatement.setString(1, voucherCode);
                preparedStatement.executeQuery();

                return resultSet.getInt("Credits");
            }
            
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        //No voucher found.
        return -1;    
    }
}
