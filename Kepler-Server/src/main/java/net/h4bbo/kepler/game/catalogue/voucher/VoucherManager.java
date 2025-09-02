package net.h4bbo.kepler.game.catalogue.voucher;

import net.h4bbo.kepler.dao.mysql.CurrencyDao;
import net.h4bbo.kepler.dao.mysql.PlayerStatisticsDao;
import net.h4bbo.kepler.dao.mysql.VoucherDao;
import net.h4bbo.kepler.game.catalogue.CatalogueItem;
import net.h4bbo.kepler.game.catalogue.CatalogueManager;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.misc.purse.Voucher;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;
import net.h4bbo.kepler.log.Log;
import net.h4bbo.kepler.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class VoucherManager {
    private static VoucherManager instance;

    public VoucherManager() {

    }

    public VoucherRedeemStatus redeem(PlayerDetails playerDetails, VoucherRedeemMode voucherRedeemMode, String voucherCode, List<CatalogueItem> redeemedItems, AtomicInteger redeemedCredits) {
        //Check and get voucher
        Voucher voucher = VoucherDao.redeemVoucher(voucherCode, playerDetails.getId());

        //No voucher was found
        if (voucher == null) {
            return VoucherRedeemStatus.FAILURE;
        }

        if (!voucher.isAllowNewUsers()) {
            int daysSince = (int) Math.floor(TimeUnit.SECONDS.toHours(PlayerStatisticsDao.getStatisticLong(playerDetails.getId(), PlayerStatistic.ONLINE_TIME)));

            if (daysSince < 1) {
                return VoucherRedeemStatus.FAILURE_NEW_ACCOUNT;
            }
        }

        //Redeem items
        List<Item> items = new ArrayList<>();

        for (String catalogueSaleCode : voucher.getItems()) {
            var catalogueItem = CatalogueManager.getInstance().getCatalogueItem(catalogueSaleCode);

            if (catalogueItem == null) {
                Log.getErrorLogger().error("Could not redeem voucher " + voucherCode + " with sale code: " + catalogueSaleCode);
                continue;
            }

            redeemedItems.add(catalogueItem);

            try {
                items.addAll(CatalogueManager.getInstance().purchase(playerDetails, catalogueItem, "", null, DateUtil.getCurrentTimeSeconds()));
            } catch (Exception ex) {

            }
        }

        /*if (items.size() > 0) {
            if (voucherRedeemMode == VoucherRedeemMode.IN_GAME) {
                if (player != null) {
                    player.getInventory().getView("new");
                }
            }
        }*/

        VoucherDao.logVoucher(voucherCode, playerDetails.getId(), voucher.getCredits(), redeemedItems);

        //This voucher gives credits, so increase credits balance
        if (voucher.getCredits() > 0) {
            CurrencyDao.increaseCredits(playerDetails, voucher.getCredits());
            redeemedCredits.set(voucher.getCredits());
        }

        return VoucherRedeemStatus.SUCCESS;
    }


    /**
     * Get the {@link VoucherManager} instance
     *
     * @return the catalogue manager instance
     */
    public static VoucherManager getInstance() {
        if (instance == null) {
            instance = new VoucherManager();
        }

        return instance;
    }

    /**
     * Resets the catalogue manager singleton.
     */
    public static void reset() {
        instance = null;
        VoucherManager.getInstance();
    }
}
