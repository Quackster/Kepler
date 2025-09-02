package net.h4bbo.kepler.dao.mysql;

import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.kepler.game.catalogue.CatalogueItem;
import net.h4bbo.kepler.game.misc.purse.Voucher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoucherDao {

    /**
     * Redeems a voucher.
     * Gets the voucher and then deletes it from the voucher table.
     *
     * @param voucherCode the string voucher code to redeem
     * @return the amount of credits redeemed or -1 if no voucher was found.
     */
    public static Voucher redeemVoucher(String voucherCode, int userId) {
        Voucher voucher = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ResultSet resultSet2 = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();

            //Get the voucher
            preparedStatement = Storage.getStorage().prepare("SELECT credits,is_single_use,allow_new_users FROM vouchers WHERE voucher_code = ? " +
                    "AND (expiry_date IS NULL OR (UNIX_TIMESTAMP() < UNIX_TIMESTAMP(expiry_date))) AND " +
                    "NOT EXISTS (SELECT vouchers_history.user_id FROM vouchers_history WHERE vouchers_history.user_id = ? AND vouchers_history.voucher_code = ?)", sqlConnection);
            preparedStatement.setString(1, voucherCode);
            preparedStatement.setInt(2, userId);
            preparedStatement.setString(3, voucherCode);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                boolean isSingleUse = resultSet.getBoolean("is_single_use");
                voucher = new Voucher(resultSet.getInt("credits"), resultSet.getBoolean("allow_new_users"));

                //Get related voucher items
                preparedStatement = Storage.getStorage().prepare("SELECT catalogue_sale_code FROM vouchers_items INNER JOIN catalogue_items ON catalogue_items.sale_code = vouchers_items.catalogue_sale_code WHERE voucher_code = ?", sqlConnection);
                preparedStatement.setString(1, voucherCode);
                resultSet2 = preparedStatement.executeQuery();

                //Find all items
                while (resultSet2.next()) {
                    voucher.getItems().add(resultSet2.getString("catalogue_sale_code"));
                }

                //Delete the voucher and related items if it's single use only
                if (isSingleUse) {
                    preparedStatement = Storage.getStorage().prepare("DELETE FROM vouchers WHERE voucher_code = ?", sqlConnection);
                    preparedStatement.setString(1, voucherCode);
                    preparedStatement.executeQuery();

                    preparedStatement = Storage.getStorage().prepare("DELETE FROM vouchers_items WHERE voucher_code = ?", sqlConnection);
                    preparedStatement.setString(1, voucherCode);
                    preparedStatement.executeQuery();
                }
            }
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);

            if (resultSet2 != null)
                Storage.closeSilently(resultSet2);

            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return voucher;
    }

    public static void logVoucher(String voucherCode, int userId, int creditsRedeemed, List<CatalogueItem> itemsRedeemed) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO vouchers_history (voucher_code, user_id, credits_redeemed, items_redeemed) VALUES (?, ?, ?, ?)", sqlConnection);
            preparedStatement.setString(1, voucherCode);
            preparedStatement.setInt(2, userId);

            if (creditsRedeemed > 0) {
                preparedStatement.setInt(3, creditsRedeemed);
            } else {
                preparedStatement.setNull(3, Types.NULL);
            }

            if (itemsRedeemed.size() > 0) {
                // Clear all duplicated items
                Map<String, Integer> distinctItems = new HashMap<>();

                for (CatalogueItem item : itemsRedeemed) {
                    if (distinctItems.containsKey(item.getSaleCode())) {
                        distinctItems.put(item.getSaleCode(), distinctItems.get(item.getSaleCode()) + 1);
                    } else {
                        distinctItems.put(item.getSaleCode(), 1);
                    }
                }

                StringBuilder stringBuilder = new StringBuilder();

                for (Map.Entry<String, Integer> kvp : distinctItems.entrySet()) {
                    stringBuilder.append(kvp.getValue());
                    stringBuilder.append(",");
                    stringBuilder.append(kvp.getKey());
                    stringBuilder.append("|");
                }

                preparedStatement.setString(4, stringBuilder.toString().substring(0, stringBuilder.length() - 1));
            } else {
                preparedStatement.setNull(4, Types.NULL);
            }

            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}

